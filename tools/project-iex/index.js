const argv = require('optimist').argv;
const Promise = require('bluebird');
const fs = require('fs-extra');
const path = require('path');
const unzip = require('unzip');
const MongoClient = require('mongodb').MongoClient;

Promise.promisifyAll(MongoClient);

// check command line arguments
if ((typeof argv.import === "undefined" && typeof argv.export === "undefined")
  || (argv.import && argv.export)) {
  throw "Please pass either --import or --export";
}
if (typeof argv.projectName !== 'string') {
  throw "Please pass --projectName=...";
}

const settings = {
  host: argv.host || 'localhost',
  port: argv.port || 27017,
  database: argv.database || 'zeta',  

  getConnectionUrl() {
    return `mongodb://${this.host}:${this.port}/`;
  }
};

const prettify = (json) => {
  return JSON.stringify(json, null, 2);
};

MongoClient.connectAsync(settings.getConnectionUrl())
.then(client => {  
  const db = client.db(settings.database);
  console.log('connected to ' + settings.getConnectionUrl());  
  if (argv.import) {
    importProject(client, db);
  } else {
    exportProject(client, db, argv.projectName);
  }    
})
.catch(err => {
  console.error(err);
});

const importProject = (client, db) => {  
  const dir = 'import';
  fs.readdir('import', (err, files) => {
    if (err) throw err;    
    const zipFiles = files.filter(file => path.extname(file) === '.zip');                          
    if (zipFiles.length === 0) {
      console.error('found no zip file to import');
      client.close();
      process.exit(1);
    }    
    if (zipFiles.length > 1) {            
      console.error('found multiple zip files to import!', zipFiles);
      client.close();
      process.exit(1);
    }    
    // unzip archive
    const zipFile = `${dir}/${zipFiles[0]}`;    
    fs.createReadStream(zipFile).pipe(unzip.Extract({path: dir}));
    client.close();
    // import files        
  });      
};


const exportProject = (client, db, projectName) => {
  console.log(`try to export project '${projectName}' ...`);
  
  const dir = './export';
  db.collection('GdslProject').findOne({name: projectName})
  .then(gdslProject => {
    const dirPath =`${dir}/${projectName}`;
    if (gdslProject === null) {
      console.error(`no project named '${projectName}' found ...`);
      client.close();
    } else {
      // remove existing directory            
      const projectId = gdslProject._id;
      db.collection('GraphicalDslInstance').findOne({graphicalDslId: projectId})
      .then(gdslInstance => {        
        if (fs.existsSync(dirPath)) {
          fs.rmdirSync(dirPath);
        }      
        fs.mkdir(dirPath, (err) => {
          if (err) console.error(`could not create directory ${dirPath}`, err);
          else {
            fs.writeFileSync(`${dirPath}/gdslProject.json`, prettify(gdslProject));
            fs.writeFileSync(`${dirPath}/gdslInstance.json`, prettify(gdslInstance));
          }          
        });        
        client.close();
      })
      .catch(err => {        
        client.close();
        throw err;
      });      
    }    
  })
  .catch(err => {
    client.close();
    throw err;
  });
};
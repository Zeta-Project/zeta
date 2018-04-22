const argv = require('optimist').argv;
const MongoClient = require('mongodb').MongoClient;
const exportProject = require('./export-project');
const importProject = require('./import-project');

const printHelp = () => {
  console.log('Usage:');
  console.log('  1. Export project:');
  console.log('     This will export the project to the \'export\' folder');
  console.log('     example: node index.js --export --projectName=nameOfTheProjectToExport');
  console.log();
  console.log('  2. Import project:');
  console.log('     The project must exist as a zip file in the \'import\' folder');
  console.log('     example: node index.js --import --projectName=nameOfTheProjectToImport');
};

const checkArgs = (argv) => {
  if ((typeof argv.import === "undefined" && typeof argv.export === "undefined") || (argv.import && argv.export)) {
    return {ok: false, msg: 'Please pass either --import or --export'};  
  }
  if (typeof argv.projectName !== 'string') {
    return {ok: false, msg: 'Please pass --projectName=...'};    
  }
  return {ok: true, msg: ''};
};

const getSettings = (argv) => {
  return {
    host: argv.host || 'localhost',
    port: argv.port || 27017,
    database: argv.database || 'zeta',  

    getConnectionUrl() {
      return `mongodb://${this.host}:${this.port}/`;
    }
  };
};

const run = async (argv) => {
  if (argv.h || argv.help) {
    printHelp();
    process.exit(0);
  }
  const {ok, msg} = checkArgs(argv);
  if (!ok) {
    console.error(msg);
    process.exit(1);
  }

  const settings = getSettings(argv);
  const client = await MongoClient.connect(settings.getConnectionUrl());
  const db = client.db(settings.database);  

  try {
    const projectName = argv.projectName;    
    if (argv.import) {
      await importProject(db, projectName);
      console.log(`successfully imported project '${projectName}'`);
    } else {
      await exportProject(db, projectName);
      console.log(`successfully exported project '${projectName}'`);
    }
  } catch(e) {
    console.error(e);
  } finally {
    client.close();  
  }
};

run(argv)
.catch(err => console.error(err));
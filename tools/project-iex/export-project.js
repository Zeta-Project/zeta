const fs = require('fs-extra');
const AdmZip = require('adm-zip');

const prettify = (json) => JSON.stringify(json, null, 2);

const zipProject = ({gdslProject, gdslInstances}) => {
  const dir = 'export';
  if (!fs.exists(dir)) {
    fs.mkdirpSync(dir);
  }
  const zip = new AdmZip();
  zip.addFile('gdslProject.json', new Buffer(prettify(gdslProject)));
  gdslInstances.forEach((gdslInstance, index) => {
    zip.addFile(`gdslInstance${index}.json`, new Buffer(prettify(gdslInstance)));
  });
  zip.writeZip(`${dir}/${gdslProject.name}.zip`);
};

const getProjectDocuments = async (db, projectName) => {
  const gdslProject = await db.collection('GdslProject').findOne({name: projectName});
  if (gdslProject === null) {
    throw `no project named '${projectName}' found ...`;
  }
  const projectId = gdslProject._id;
  const gdslInstances = await db.collection('GraphicalDslInstance').find({graphicalDslId: projectId}).toArray();
  return {gdslProject, gdslInstances};
};

const exportProject = async (db, projectName) => {
  const documents = await getProjectDocuments(db, projectName);
  zipProject(documents);
};

module.exports = exportProject;
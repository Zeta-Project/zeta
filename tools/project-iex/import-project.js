const fs = require('fs-extra');
const AdmZip = require('adm-zip');

const readJson = async (filepath) => {
  const buf = await fs.readFile(filepath);
  const str = buf.toString();
  const jsn = JSON.parse(str);
  return jsn;
};

const unzipProject = async (projectName) => {
  const dir = 'import';
  const filepath = `${dir}/${projectName}.zip`;
  if (!fs.existsSync(filepath)) {
    throw `file not found: ${filepath}`;
  }
  const zip = new AdmZip(filepath);
  const overwrite = true;
  const unzipDir = `${dir}/${projectName}`;
  zip.extractAllTo(unzipDir, overwrite);
  const filenames = await fs.readdir(unzipDir);
  const gdslProject = await readJson(`${unzipDir}/gdslProject.json`);
  const gdslInstanceNames = filenames.filter(f => f.startsWith('gdslInstance'));
  const promises = gdslInstanceNames.map(f => readJson(`${unzipDir}/${f}`));
  const gdslInstances = await Promise.all(promises);
  await fs.remove(unzipDir);
  return {gdslProject, gdslInstances};
};

const upsertGdslProject = async (db, gdslProject) => {
  const query = {'_id': gdslProject._id};
  const update = gdslProject;
  const options = {upsert: true};
  await db.collection('GdslProject').update(query, update, options);
};

const upsertGdslInstances = async (db, gdslInstances) => {
  if (gdslInstances.length > 0) {
    const bulk = db.collection('GraphicalDslInstance').initializeOrderedBulkOp();
    gdslInstances.forEach(gdslInstance => {
      const query = {'_id': gdslInstance._id};
      const update = gdslInstance;
      bulk.find(query).upsert().updateOne(update);
    });
    await bulk.execute();
  }
};

const updateGdslProjectAuthorizations = async (db, gdslProject) => {
  const projectId = gdslProject._id;
  const query = {};
  const update = {
    '$addToSet': {
      'authorizedEntityAccess.GdslProject': projectId
    }
  };
  await db.collection('AccessAuthorisation').update(query, update);
};

const updateGdslInstanceAuthorizations = async (db, gdslInstances) => {
  if (gdslInstances.length >= 0) {
    const gdslInstanceIds = gdslInstances.map(instance => instance._id);
    const query = {};
    const update = {
      '$addToSet': {
        'authorizedEntityAccess.GraphicalDslInstance': {'$each': gdslInstanceIds}
      }
    };
    await db.collection('AccessAuthorisation').update(query, update);
  }
};

const upsertDocuments = async (db, {gdslProject, gdslInstances}) => {
  await upsertGdslProject(db, gdslProject);
  await upsertGdslInstances(db, gdslInstances);
  await updateGdslProjectAuthorizations(db, gdslProject);
  await updateGdslInstanceAuthorizations(db, gdslInstances);
};

const importProject = async (db, projectName) => {
  const documents = await unzipProject(projectName);
  await upsertDocuments(db, documents);
};

module.exports = importProject;
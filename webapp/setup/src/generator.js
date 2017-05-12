// helper class to create documentd and correctly set the id for the
// documents.
class Generator {
  constructor(system) {
    this.system = system;
    this.docs = [];
  }

  id(...rest) {
    let str = "";
    for (let i = 0; i < rest.length; i++) {
      str += rest[i];
      if (i !== arguments.length - 1) {
        str += '-';
      }
    }
    return str;
  }

  rand() {
    const size = 16;
    const charSet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'; // abcdefghijklmnopqrstuvwxyz
    let text = "";
    for (let i = 0; i < size; i++) {
      text += charSet.charAt(Math.floor(Math.random() * charSet.length));
    }
    return text;
  }

  insert(doc, user) {
    this.docs.push({ doc, user: user.name });
  }

  GeneratorImage(name, dockerImage, description, options) {
    const doc = {
      _id: this.id('GeneratorImage', this.rand()),
      type: 'GeneratorImage',
      name,
      dockerImage,
      description,
      options
    };
    this.insert(doc, this.system);
    return this;
  }

  getDocs() {
    return this.docs;
  }
}

module.exports = Generator;

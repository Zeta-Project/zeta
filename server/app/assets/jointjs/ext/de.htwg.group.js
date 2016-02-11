/*
 * This class is needed to group elements.
 * An element can be only in one group and a group can be in another group.
 * Because of this recursive grouping is possible.
 * @author: Maximilian Göke
 */


/* Constructor */
function htwgGroup() {
  this.groupedElements = {};
  this.groups = {};
}

htwgGroup.prototype = {

  /* Finds group from element which has the highest level */
  findGroupFromElement: function(elementID) {
    // element is no group
    if(this.groupedElements[elementID] === undefined) return undefined;
    // element is in a group
    return findGroup(this.groupedElements[elementID], this);

    /* Finds the right group recursively*/
    function findGroup(groupID, context) {
      if(context.groupedElements[groupID] === undefined) return groupID;
      return findGroup(context.groupedElements[groupID], context);
    }
  },

  /* Returns all element ids from a group */
  getElementIDsFromGroup: function(groupID) {
    // group doesn't exist
    if(this.groups[groupID] === undefined) return [null];
    // returns all elements
    return getElementIDs(groupID, this);

    /* Finds all element ids recursively */
    function getElementIDs(id, context) {
      var elements = context.groups[id];
      if(elements === undefined) return [id];
      var result = [];
      elements.forEach(function (id) {
        result = _.union(result, getElementIDs(id, context) || id);
      });

      return result;
    }
  },

  /* Adds element to group */
  add: function(elementID, groupID) {
    // check if element is already part of a group
    if(this.groupedElements[elementID] !== undefined) { elementID = findGroup(elementID, this); }
    /*console.log(elementID)
    console.log(this.groupedElements);
    console.log(this.groups);*/
    this.groupedElements[elementID] = groupID;
    if(this.groups[groupID] === undefined) this.groups[groupID] = [];
    if(!(_.contains(this.groups[groupID], elementID))) this.groups[groupID].push(elementID);

    /* Finds the right group recursively*/
    function findGroup(elementID, context) {
      if(context.groupedElements[elementID] == groupID || context.groupedElements[elementID] === undefined) return elementID;
      return findGroup(context.groupedElements[elementID], context);
    }
  },

  /* Removes a group */
  removeGroup: function(groupID) {
    var elementList = this.groups[groupID];
    if(elementList !== undefined) {
      // delete each dependency from an element to this group
      elementList.forEach( function(id) {
        var element = this.groupedElements[id];
        if(element !== undefined) this.groupedElements[id] = undefined;
      }, this);
    }
    // alot faster than "delete this.groups[groupID]"
    // this.groups[groupID] = undefined;
    delete this.groups[groupID];
  },

  /* Removes all groups where elementID is a member */
  removeFirstGroup: function(elementID) {
    while(this.isInAGroup(elementID)) {
      var groupID = this.findGroupFromElement(elementID);
      this.removeGroup(this.findGroupFromElement(elementID));
      delete this.groupedElements[groupID];
    }

    this.groupedElements[elementID] = undefined;
  },

  /* Removes an Element from the lists */
  removeElement: function(elementID) {
    this.removeFirstGroup(elementID);

    delete this.groupedElements[elementID];
  },

  /* Checks if an element is in a group */
  isInAGroup: function(elementID) {
    return this.groupedElements[elementID] !== undefined;
  }
};

root = exports ? this

root.jQuery(document).ready(() ->
  container = document.documentElement
  popup = document.querySelector( '.nodeCodeEditor-popup' )
  cover = document.querySelector( '.nodeCodeEditor-cover' )
  currentState = null;

  addClass = (element, name) ->
    element.className = element.className.replace( /\s+$/gi, '' ) + ' ' + name;

  removeClass = (element, name) ->
    element.className = element.className.replace( name, '' );


  addClass( container, 'nodeCodeEditor-ready' );

  #deactivate on ESC
  onDocumentKeyUp = (event) ->
    deactivate() if event.keyCode == 27

  #deactivate on click outside
  onDocumentClick = (event) ->
    deactivate() if event.target == cover

  activate = (context) ->
    document.addEventListener( 'keyup', onDocumentKeyUp, false );
    document.addEventListener( 'click', onDocumentClick, false );
    removeClass( popup, currentState );
    addClass( popup, 'no-transition' );
    addClass( popup, null );

    setTimeout( () ->
      removeClass( popup, 'no-transition' );
      addClass( container, 'nodeCodeEditor-active' );
    , 0 );

    currentState = null;
    root.dataVisEditor = DataVisEditor()
    root.dataVisEditor.load(context)


  deactivate = () ->
    document.removeEventListener( 'keyup', onDocumentKeyUp, false );
    document.removeEventListener( 'click', onDocumentClick, false );
    removeClass( container, 'nodeCodeEditor-active' );
    root.dataVisEditor.unload() if root.dataVisEditor?
    root.dataVisEditor = null

  displayErrors = (errors) ->
    root.jQuery("#validation-errors").append("<li>"+error+"</li>") for error in errors
    root.jQuery("#validation-errors").show()

  hideErrors = () -> root.jQuery("#validation-errors").hide()

  disableBlur = () ->
    addClass( document.documentElement, 'no-blur' );

  window.nodeCodeEditor =
    activate: activate
    deactivate: deactivate
    disableBlur: disableBlur
    displayErrors: displayErrors
    hideErrors: hideErrors
);
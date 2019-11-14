/**
 * Binds the given command to the input element specified by the given selector.
 *
 * @param {string} selector
 * @param {ICommand} command
 * @param {CanvasComponent|GraphComponent} target
 * @param {Object?} parameter
 */
export function bindCommand(selector, command, target, parameter) {
    const element = document.querySelector(selector)
    if (arguments.length < 4) {
        parameter = null
        if (arguments.length < 3) {
            target = null
        }
    }
    if (!element) {
        return
    }
    command.addCanExecuteChangedListener((sender, e) => {
        if (command.canExecute(parameter, target)) {
            element.removeAttribute('disabled')
        } else {
            element.setAttribute('disabled', 'disabled')
        }
    })
    element.addEventListener('click', e => {
        if (command.canExecute(parameter, target)) {
            command.execute(parameter, target)
        }
    })
}

/** @return {Element} */
export function addClass(/**Element*/ e, /**string*/ className) {
    const classes = e.getAttribute('class')
    if (classes === null || classes === '') {
        e.setAttribute('class', className)
    } else if (!hasClass(e, className)) {
        e.setAttribute('class', `${classes} ${className}`)
    }
    return e
}

/** @return {Element} */
export function removeClass(/**Element*/ e, /**string*/ className) {
    const classes = e.getAttribute('class')
    if (classes !== null && classes !== '') {
        if (classes === className) {
            e.setAttribute('class', '')
        } else {
            const result = classes
                .split(' ')
                .filter(s => s !== className)
                .join(' ')
            e.setAttribute('class', result)
        }
    }
    return e
}

/** @return {boolean} */
export function hasClass(/**Element*/ e, /**string*/ className) {
    const classes = e.getAttribute('class')
    const r = new RegExp(`\\b${className}\\b`, '')
    return r.test(classes)
}

/** @return {Element} */
export function toggleClass(/**Element*/ e, /**string*/ className) {
    if (hasClass(e, className)) {
        removeClass(e, className)
    } else {
        addClass(e, className)
    }
    return e
}

/** @return snapping action binding */
export function bindAction(/**string*/ selector, /**function(Event)*/ action) {
    const element = document.querySelector(selector)
    if (!element) {
        return
    }
    element.addEventListener('click', e => {
        action(e)
    })


}
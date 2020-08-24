export function open(accessor = "PropertiesSidebar") {
    if (accessor)
        document.getElementById(accessor).className = "sidebar right";
}

export function close(accessor = "PropertiesSidebar") {
    if (accessor)
        document.getElementById(accessor).className = ".right-hidden .sidebar.right"
}
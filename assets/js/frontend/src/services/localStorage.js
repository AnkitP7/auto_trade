class LocalStorage {

    set = (key, value)  => localStorage.setItem(key, value)

    get = (key) => localStorage.getItem(key)

    get_or_set = (key, value) => !localStorage.getItem(key) ? localStorage.setItem(key, value) : localStorage.getItem(key)
}

export default LocalStorage
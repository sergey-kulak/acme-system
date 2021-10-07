export class Cache {
    constructor() {
        this.store = {}
        this.timerId = setInterval(() => this._clear(), 15000)
    }

    _clear() {
        let now = new Date()
        for (let regionName in this.store) {
            let region = this.store[regionName]
            for (let key in region) {
                let cacheItem = region[key]
                if (cacheItem.expired && cacheItem.expired < now) {
                    delete region[key]
                }

            }
        }
    }

    invalidate(regionName) {
        this.store[regionName] = {}
    }

    getValue(regionName, key) {
        let region = this.store[regionName] || {}
        let cacheItem = region[key]
        return cacheItem && cacheItem.value
    }

    setValue(regionName, key, value, expiredInSeconds) {
        let region = this.store[regionName]
        if (!region) {
            region = {}
            this.store[regionName] = region
        }
        region[key] = {
            value: value,
            expired: expiredInSeconds ?
                new Date(new Date().getTime() + expiredInSeconds * 1000) : undefined
        }
    }

    retriveIfAbsent(regionName, key, provider, expiredInSeconds) {
        let cacheItem = this.getValue(regionName, key)
        if (cacheItem) {
            return cacheItem
        } else {
            let value = provider()
            this.setValue(regionName, key, value, expiredInSeconds)
            return value
        }
    }
}

export const GLOBAL_CACHE = new Cache()
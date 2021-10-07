export const ROLE = {
    ADMIN: 'ADMIN',
    COMPANY_OWNER: 'COMPANY_OWNER',
    PP_MANAGER: 'PP_MANAGER',
    WAITER: 'WAITER',
    ACCOUNTANT: 'ACCOUNTANT',
    CHEF: 'CHEF',
    COOK: 'COOK'
}

const ROLE_HIERARHY = {
    ADMIN: [ROLE.COMPANY_OWNER, ROLE.PP_MANAGER, ROLE.WAITER, ROLE.ACCOUNTANT, ROLE.COOK, ROLE.CHEF],
    COMPANY_OWNER: [ROLE.PP_MANAGER, ROLE.WAITER, ROLE.COOK, ROLE.CHEF],
    PP_MANAGER: [ROLE.WAITER, ROLE.COOK, ROLE.CHEF],
    CHEF: [ROLE.COOK]
}

export const getAllAccessibleRoles = (auth) => {
    let userRole = auth.user.role
    return [userRole].concat(ROLE_HIERARHY[userRole] || [])
}

export const hasRole = (auth, ...roles) => {
    return auth.isAuthenticated
        && getAllAccessibleRoles(auth).some(userRole => roles.includes(userRole))
}

export const hasExactRole = (auth, ...roles) => {
    return auth.isAuthenticated && roles.includes(auth.user.role)
}
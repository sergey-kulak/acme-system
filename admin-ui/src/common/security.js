export const ROLE = {
    ADMIN: 'ADMIN',
    COMPANY_OWNER: 'COMPANY_OWNER',
    PP_MANAGER: 'PP_MANAGER',
    WAITER: 'WAITER',
    ACCOUNTANT: 'ACCOUNTANT',
    COOK: 'COOK'
}

const ROLE_HIERARHY = {
    ADMIN: [ROLE.COMPANY_OWNER, ROLE.PP_MANAGER, ROLE.WAITER, ROLE.ACCOUNTANT, ROLE.COOK],
    COMPANY_OWNER: [ROLE.PP_MANAGER, ROLE.WAITER, ROLE.COOK],
    PP_MANAGER: [ROLE.WAITER, ROLE.COOK]
}

export const getAllAccessibleRoles = (auth) => {
    let userRole = auth.user.role;
    return [userRole].concat(ROLE_HIERARHY[userRole] || []);
}

export const hasRole = (auth, ...roles) => {
    return auth.isAuthenticated
        && getAllAccessibleRoles(auth).some(userRole => roles.includes(userRole));
}
package com.leopardseal.inventorymanagerapp.responses

data class SignInResponse (var myUser: MyUsers, var userRoles: List<UserRoles>, var orgs: List<Orgs>, var images: List<Images>, var roles: List<Roles>)
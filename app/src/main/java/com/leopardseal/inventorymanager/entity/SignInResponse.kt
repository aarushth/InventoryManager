package com.leopardseal.inventorymanager.entity

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse (var myUser: MyUsers, var userRoles: List<UserRoles>, var orgs: List<Orgs>, var images: List<Images>, var roles: List<Roles>)
package com.example.vibees.screens.home.host

import java.util.UUID

// class to store data when filling in from host screen
data class PartyStore(
    var street: String? = null,
    var city: String? = null,
    var prov: String? = null,
    var postal_code: String? = null,
    var date_time: String? = null,
    var name: String? = null,
    var type: String? = null,
    var entry_fee: Int? = null,
    var desc: String? = null,
    var drug: Boolean? = null,
    var byob: Boolean? = null,
    var taglist: List<String>? = null,
    var image: String? = null,
    var user_id: UUID? = null,
    var max_cap: Int? = null,
    var party_id: String? = null,
    var host_name: String? = null,
    var qr_endpoint: String? = null,
    // this keeps track of whether edit or host form
    var isedit: Boolean,
)

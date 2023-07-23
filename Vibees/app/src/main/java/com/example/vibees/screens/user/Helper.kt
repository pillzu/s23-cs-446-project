package com.example.vibees.screens.user

import com.example.vibees.Models.Party
import java.time.LocalDateTime

class Helper {
    companion object {
        fun sortPartiesBy(
            parties: List<Party>,
            factor: String,
            city: String = "",
            prov: String = ""
        ): List<Party> {
            when (factor) {
                "Proximity" -> {
                    return parties.sortedWith <Party> (object: Comparator <Party> {
                        override fun compare (p0: Party, p1: Party) : Int {
                            if (p0.city != p1.city) {
                                if (p0.city == city) return -1
                                else if (p1.city == city) return 1
                            }

                            if (p0.prov != p1.prov) {
                                if (p0.prov == prov) return -1
                                else if (p1.prov == prov) return 1
                            }

                            return 0
                        }
                    })
                }

                "Price" -> {
                    return parties.sortedBy { it.entry_fee }
                }

                "Date" -> {
                    return parties.sortedBy { it.date_time }
                }

                "Capacity" -> {
                    return parties.sortedByDescending { it.max_cap }
                }
            }

            return parties
        }


        fun queryPartiesByTags(
            parties: List<Party>,
            tag: String
        ): List<Party> {
            var matchedParties = parties.filter {
                    party -> tag == party.type
            }
            return matchedParties
        }


        fun searchParties(
            parties: List<Party>,
            keyword: String
        ): List<Party> {
            var matchedParties = parties.filter {
                    party -> filterPartyByKeyword(party, keyword)
            }
            return matchedParties
        }


        private fun filterPartyByKeyword (
            party: Party,
            keyword: String
        ): Boolean {
            // verify name
            if (party.name != null && party.name.contains(keyword))
                return true

            // verify address
            if (party.street.contains(keyword))
                return true
            if (party.city.contains(keyword))
                return true
            if (party.prov.contains(keyword))
                return true

            // verify host name
            if (party.host_name != null && party.host_name.contains(keyword))
                return true

            // verify description
            if (party.desc.contains(keyword))
                return true

            return false
        }
    }
}
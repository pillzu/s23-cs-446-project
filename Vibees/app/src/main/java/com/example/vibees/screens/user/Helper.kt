package com.example.vibees.screens.user

import android.annotation.SuppressLint
import android.util.Log
import com.example.vibees.Models.Party
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class Helper {
    companion object {
        @SuppressLint("SimpleDateFormat")
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

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
                    return parties.sortedWith <Party> (object: Comparator <Party> {
                        override fun compare (p0: Party, p1: Party) : Int {
                            Log.d("TAG sort", "Comparing A: ${p0.name}")
                            val date1 = formatter.parse(p0.date_time)
                            Log.d("TAG sort", "Comparing B: ${p1.name}")
                            val date2 = formatter.parse(p1.date_time)
                            return date1.compareTo(date2)
                        }
                    })
                }

                "Capacity" -> {
                    return parties.sortedByDescending { it.max_cap }
                }
            }

            return parties
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
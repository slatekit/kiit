/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

/**
 * Represents a country with iso code, name, and phone codes for sms features
 *
 * @param iso   : 2 char iso code
 * @param iso3  : 3 char iso code
 * @param phone : international dialing code
 * @param name  : name of country
 */
data class Country(val iso: String, val iso3: String, val phone: String, val name: String) {


    companion object {

        /**
         * get the countries by list of iso codes
         * @param codes
         * @return
         */
        fun filter(codes: List<CountryCode>): List<Country> {
            val countries = all
            val filtered = codes.map { code -> countries.find { country -> country.iso == code.value } }
            val matched = filtered.filterNotNull()
            return matched
        }


        fun find(code:String?): Country? {
            return when(code) {
                null -> null
                ""   -> usa
                else -> all.filter { it.iso == code.toUpperCase() }.first()
            }
        }


        val all = listOf(
            Country("AF", "AFG", "93", "Afghanistan"),
            Country("AL", "ALB", "355", "Albania"),
            Country("DZ", "DZA", "213", "Algeria"),
            Country("AS", "ASM", "1-684", "American Samoa"),
            Country("AD", "AND", "376", "Andorra"),
            Country("AO", "AGO", "244", "Angola"),
            Country("AI", "AIA", "1-264", "Anguilla"),
            Country("AQ", "ATA", "672", "Antarctica"),
            Country("AG", "ATG", "1-268", "Antigua and Barbuda"),
            Country("AR", "ARG", "54", "Argentina"),
            Country("AM", "ARM", "374", "Armenia"),
            Country("AW", "ABW", "297", "Aruba"),
            Country("AU", "AUS", "61", "Australia"),
            Country("AT", "AUT", "43", "Austria"),
            Country("AZ", "AZE", "994", "Azerbaijan"),
            Country("BS", "BHS", "1-242", "Bahamas"),
            Country("BH", "BHR", "973", "Bahrain"),
            Country("BD", "BGD", "880", "Bangladesh"),
            Country("BB", "BRB", "1-246", "Barbados"),
            Country("BY", "BLR", "375", "Belarus"),
            Country("BE", "BEL", "32", "Belgium"),
            Country("BZ", "BLZ", "501", "Belize"),
            Country("BJ", "BEN", "229", "Benin"),
            Country("BM", "BMU", "1-441", "Bermuda"),
            Country("BT", "BTN", "975", "Bhutan"),
            Country("BO", "BOL", "591", "Bolivia"),
            Country("BA", "BIH", "387", "Bosnia and Herzegovina"),
            Country("BW", "BWA", "267", "Botswana"),
            Country("BR", "BRA", "55", "Brazil"),
            Country("IO", "IOT", "246", "British Indian Ocean Territory"),
            Country("VG", "VGB", "1-284", "British Virgin Islands"),
            Country("BN", "BRN", "673", "Brunei"),
            Country("BG", "BGR", "359", "Bulgaria"),
            Country("BF", "BFA", "226", "Burkina Faso"),
            Country("BI", "BDI", "257", "Burundi"),
            Country("KH", "KHM", "855", "Cambodia"),
            Country("CM", "CMR", "237", "Cameroon"),
            Country("CA", "CAN", "1", "Canada"),
            Country("CV", "CPV", "238", "Cape Verde"),
            Country("KY", "CYM", "1-345", "Cayman Islands"),
            Country("CF", "CAF", "236", "Central African Republic"),
            Country("TD", "TCD", "235", "Chad"),
            Country("CL", "CHL", "56", "Chile"),
            Country("CN", "CHN", "86", "China"),
            Country("CX", "CXR", "61", "Christmas Island"),
            Country("CC", "CCK", "61", "Cocos Islands"),
            Country("CO", "COL", "57", "Colombia"),
            Country("KM", "COM", "269", "Comoros"),
            Country("CK", "COK", "682", "Cook Islands"),
            Country("CR", "CRI", "506", "Costa Rica"),
            Country("HR", "HRV", "385", "Croatia"),
            Country("CU", "CUB", "53", "Cuba"),
            Country("CW", "CUW", "599", "Curacao"),
            Country("CY", "CYP", "357", "Cyprus"),
            Country("CZ", "CZE", "420", "Czech Republic"),
            Country("CD", "COD", "243", "Democratic Republic of the Congo"),
            Country("DK", "DNK", "45", "Denmark"),
            Country("DJ", "DJI", "253", "Djibouti"),
            Country("DM", "DMA", "1-767", "Dominica"),
            Country("DO", "DOM", "1-809", "Dominican Republic"),
            Country("TL", "TLS", "670", "East Timor"),
            Country("EC", "ECU", "593", "Ecuador"),
            Country("EG", "EGY", "20", "Egypt"),
            Country("SV", "SLV", "503", "El Salvador"),
            Country("GQ", "GNQ", "240", "Equatorial Guinea"),
            Country("ER", "ERI", "291", "Eritrea"),
            Country("EE", "EST", "372", "Estonia"),
            Country("ET", "ETH", "251", "Ethiopia"),
            Country("FK", "FLK", "500", "Falkland Islands"),
            Country("FO", "FRO", "298", "Faroe Islands"),
            Country("FJ", "FJI", "679", "Fiji"),
            Country("FI", "FIN", "358", "Finland"),
            Country("FR", "FRA", "33", "France"),
            Country("PF", "PYF", "689", "French Polynesia"),
            Country("GA", "GAB", "241", "Gabon"),
            Country("GM", "GMB", "220", "Gambia"),
            Country("GE", "GEO", "995", "Georgia"),
            Country("DE", "DEU", "49", "Germany"),
            Country("GH", "GHA", "233", "Ghana"),
            Country("GI", "GIB", "350", "Gibraltar"),
            Country("GR", "GRC", "30", "Greece"),
            Country("GL", "GRL", "299", "Greenland"),
            Country("GD", "GRD", "1-473", "Grenada"),
            Country("GU", "GUM", "1-671", "Guam"),
            Country("GT", "GTM", "502", "Guatemala"),
            Country("GG", "GGY", "44-1481", "Guernsey"),
            Country("GN", "GIN", "224", "Guinea"),
            Country("GW", "GNB", "245", "Guinea-Bissau"),
            Country("GY", "GUY", "592", "Guyana"),
            Country("HT", "HTI", "509", "Haiti"),
            Country("HN", "HND", "504", "Honduras"),
            Country("HK", "HKG", "852", "Hong Kong"),
            Country("HU", "HUN", "36", "Hungary"),
            Country("IS", "ISL", "354", "Iceland"),
            Country("IN", "IND", "91", "India"),
            Country("ID", "IDN", "62", "Indonesia"),
            Country("IR", "IRN", "98", "Iran"),
            Country("IQ", "IRQ", "964", "Iraq"),
            Country("IE", "IRL", "353", "Ireland"),
            Country("IM", "IMN", "44-1624", "Isle of Man"),
            Country("IL", "ISR", "972", "Israel"),
            Country("IT", "ITA", "39", "Italy"),
            Country("CI", "CIV", "225", "Ivory Coast"),
            Country("JM", "JAM", "1-876", "Jamaica"),
            Country("JP", "JPN", "81", "Japan"),
            Country("JE", "JEY", "44-1534", "Jersey"),
            Country("JO", "JOR", "962", "Jordan"),
            Country("KZ", "KAZ", "7", "Kazakhstan"),
            Country("KE", "KEN", "254", "Kenya"),
            Country("KI", "KIR", "686", "Kiribati"),
            Country("XK", "XKX", "383", "Kosovo"),
            Country("KW", "KWT", "965", "Kuwait"),
            Country("KG", "KGZ", "996", "Kyrgyzstan"),
            Country("LA", "LAO", "856", "Laos"),
            Country("LV", "LVA", "371", "Latvia"),
            Country("LB", "LBN", "961", "Lebanon"),
            Country("LS", "LSO", "266", "Lesotho"),
            Country("LR", "LBR", "231", "Liberia"),
            Country("LY", "LBY", "218", "Libya"),
            Country("LI", "LIE", "423", "Liechtenstein"),
            Country("LT", "LTU", "370", "Lithuania"),
            Country("LU", "LUX", "352", "Luxembourg"),
            Country("MO", "MAC", "853", "Macau"),
            Country("MK", "MKD", "389", "Macedonia"),
            Country("MG", "MDG", "261", "Madagascar"),
            Country("MW", "MWI", "265", "Malawi"),
            Country("MY", "MYS", "60", "Malaysia"),
            Country("MV", "MDV", "960", "Maldives"),
            Country("ML", "MLI", "223", "Mali"),
            Country("MT", "MLT", "356", "Malta"),
            Country("MH", "MHL", "692", "Marshall Islands"),
            Country("MR", "MRT", "222", "Mauritania"),
            Country("MU", "MUS", "230", "Mauritius"),
            Country("YT", "MYT", "262", "Mayotte"),
            Country("MX", "MEX", "52", "Mexico"),
            Country("FM", "FSM", "691", "Micronesia"),
            Country("MD", "MDA", "373", "Moldova"),
            Country("MC", "MCO", "377", "Monaco"),
            Country("MN", "MNG", "976", "Mongolia"),
            Country("ME", "MNE", "382", "Montenegro"),
            Country("MS", "MSR", "1-664", "Montserrat"),
            Country("MA", "MAR", "212", "Morocco"),
            Country("MZ", "MOZ", "258", "Mozambique"),
            Country("MM", "MMR", "95", "Myanmar"),
            Country("NA", "NAM", "264", "Namibia"),
            Country("NR", "NRU", "674", "Nauru"),
            Country("NP", "NPL", "977", "Nepal"),
            Country("NL", "NLD", "31", "Netherlands"),
            Country("AN", "ANT", "599", "Netherlands Antilles"),
            Country("NC", "NCL", "687", "New Caledonia"),
            Country("NZ", "NZL", "64", "New Zealand"),
            Country("NI", "NIC", "505", "Nicaragua"),
            Country("NE", "NER", "227", "Niger"),
            Country("NG", "NGA", "234", "Nigeria"),
            Country("NU", "NIU", "683", "Niue"),
            Country("KP", "PRK", "850", "North Korea"),
            Country("MP", "MNP", "1-670", "Northern Mariana Islands"),
            Country("NO", "NOR", "47", "Norway"),
            Country("OM", "OMN", "968", "Oman"),
            Country("PK", "PAK", "92", "Pakistan"),
            Country("PW", "PLW", "680", "Palau"),
            Country("PS", "PSE", "970", "Palestine"),
            Country("PA", "PAN", "507", "Panama"),
            Country("PG", "PNG", "675", "Papua New Guinea"),
            Country("PY", "PRY", "595", "Paraguay"),
            Country("PE", "PER", "51", "Peru"),
            Country("PH", "PHL", "63", "Philippines"),
            Country("PN", "PCN", "64", "Pitcairn"),
            Country("PL", "POL", "48", "Poland"),
            Country("PT", "PRT", "351", "Portugal"),
            Country("PR", "PRI", "1-787", "Puerto Rico"),
            Country("QA", "QAT", "974", "Qatar"),
            Country("CG", "COG", "242", "Republic of the Congo"),
            Country("RE", "REU", "262", "Reunion"),
            Country("RO", "ROU", "40", "Romania"),
            Country("RU", "RUS", "7", "Russia"),
            Country("RW", "RWA", "250", "Rwanda"),
            Country("BL", "BLM", "590", "Saint Barthelemy"),
            Country("SH", "SHN", "290", "Saint Helena"),
            Country("KN", "KNA", "1-869", "Saint Kitts and Nevis"),
            Country("LC", "LCA", "1-758", "Saint Lucia"),
            Country("MF", "MAF", "590", "Saint Martin"),
            Country("PM", "SPM", "508", "Saint Pierre and Miquelon"),
            Country("VC", "VCT", "1-784", "Saint Vincent and the Grenadines"),
            Country("WS", "WSM", "685", "Samoa"),
            Country("SM", "SMR", "378", "San Marino"),
            Country("ST", "STP", "239", "Sao Tome and Principe"),
            Country("SA", "SAU", "966", "Saudi Arabia"),
            Country("SN", "SEN", "221", "Senegal"),
            Country("RS", "SRB", "381", "Serbia"),
            Country("SC", "SYC", "248", "Seychelles"),
            Country("SL", "SLE", "232", "Sierra Leone"),
            Country("SG", "SGP", "65", "Singapore"),
            Country("SX", "SXM", "1-721", "Sint Maarten"),
            Country("SK", "SVK", "421", "Slovakia"),
            Country("SI", "SVN", "386", "Slovenia"),
            Country("SB", "SLB", "677", "Solomon Islands"),
            Country("SO", "SOM", "252", "Somalia"),
            Country("ZA", "ZAF", "27", "South Africa"),
            Country("KR", "KOR", "82", "South Korea"),
            Country("SS", "SSD", "211", "South Sudan"),
            Country("ES", "ESP", "34", "Spain"),
            Country("LK", "LKA", "94", "Sri Lanka"),
            Country("SD", "SDN", "249", "Sudan"),
            Country("SR", "SUR", "597", "Suriname"),
            Country("SJ", "SJM", "47", "Svalbard and Jan Mayen"),
            Country("SZ", "SWZ", "268", "Swaziland"),
            Country("SE", "SWE", "46", "Sweden"),
            Country("CH", "CHE", "41", "Switzerland"),
            Country("SY", "SYR", "963", "Syria"),
            Country("TW", "TWN", "886", "Taiwan"),
            Country("TJ", "TJK", "992", "Tajikistan"),
            Country("TZ", "TZA", "255", "Tanzania"),
            Country("TH", "THA", "66", "Thailand"),
            Country("TG", "TGO", "228", "Togo"),
            Country("TK", "TKL", "690", "Tokelau"),
            Country("TO", "TON", "676", "Tonga"),
            Country("TT", "TTO", "1-868", "Trinidad and Tobago"),
            Country("TN", "TUN", "216", "Tunisia"),
            Country("TR", "TUR", "90", "Turkey"),
            Country("TM", "TKM", "993", "Turkmenistan"),
            Country("TC", "TCA", "1-649", "Turks and Caicos Islands"),
            Country("TV", "TUV", "688", "Tuvalu"),
            Country("VI", "VIR", "1-340", "U.S. Virgin Islands"),
            Country("UG", "UGA", "256", "Uganda"),
            Country("UA", "UKR", "380", "Ukraine"),
            Country("AE", "ARE", "971", "United Arab Emirates"),
            Country("GB", "GBR", "44", "United Kingdom"),
            Country("US", "USA", "1", "United States"),
            Country("UY", "URY", "598", "Uruguay"),
            Country("UZ", "UZB", "998", "Uzbekistan"),
            Country("VU", "VUT", "678", "Vanuatu"),
            Country("VA", "VAT", "379", "Vatican"),
            Country("VE", "VEN", "58", "Venezuela"),
            Country("VN", "VNM", "84", "Vietnam"),
            Country("WF", "WLF", "681", "Wallis and Futuna"),
            Country("EH", "ESH", "212", "Western Sahara"),
            Country("YE", "YEM", "967", "Yemen"),
            Country("ZM", "ZMB", "260", "Zambia"),
            Country("ZW", "ZWE", "263", "Zimbabwe")
        )



        /**
         * country usa - used for defaults
         *
         * @return
         */
        val usa = all.find { it.iso == "US" }!!
    }

}

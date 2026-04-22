package com.example.simex_app.data.models

import com.google.gson.annotations.SerializedName

data class Usuari(
    @SerializedName("id", alternate = ["Id"]) val id: Int,
    @SerializedName("nom", alternate = ["Nom"]) val nom: String,
    @SerializedName("cognoms", alternate = ["Cognoms"]) val cognoms: String,
    @SerializedName("correu", alternate = ["Correu"]) val correu: String,
    @SerializedName("rolId", alternate = ["RolId"]) val rolId: Int,
    @SerializedName("active", alternate = ["Active"]) val active: Int,
    @SerializedName("dniFotoFrontal", alternate = ["DniFotoFrontal"]) val dniFotoFrontal: String? = null,
    @SerializedName("dniFotoTrasera", alternate = ["DniFotoTrasera"]) val dniFotoTrasera: String? = null
)
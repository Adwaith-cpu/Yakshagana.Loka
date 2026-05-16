package com.yakshaganaloka.app.models

data class Artist(
    val id: String = "",
    val name: String = "",
    val role: String = "", // Bhagavatha, Actor, Maddalegara, Himmela
    val vesha: String = "",
    val description: String = "",
    val imageUrl: String? = null
)

object ArtistDummyData {
    val artists = listOf(
        // BHAGAVATHAS
        Artist(
            id = "b1",
            name = "Kalinga Navada",
            role = "Bhagavatha",
            description = "A legendary Bhagavatha known for revolutionizing the musical aspect of Yakshagana with his distinct classical touch and soulful voice.",
            vesha = "Bhagavathike"
        ),
        Artist(
            id = "b2",
            name = "Narayana Uppoor",
            role = "Bhagavatha",
            description = "Known for his powerful voice and deep knowledge of Yakshagana Prasangas in the Badagu Thittu style. A true traditionalist.",
            vesha = "Bhagavathike"
        ),
        Artist(
            id = "b3",
            name = "Balipa Narayana Bhagavatha",
            role = "Bhagavatha",
            description = "The patriarch of the Balipa style of singing, carrying forward a legacy of over seven decades in Tenku Thittu.",
            vesha = "Bhagavathike"
        ),
        Artist(
            id = "b4",
            name = "Padyana Ganapathi Bhat",
            role = "Bhagavatha",
            description = "A versatile Bhagavatha known for his melodious rendering and command over the raga system in Tenku Thittu.",
            vesha = "Bhagavathike"
        ),
        
        // ACTORS
        Artist(
            id = "a1",
            name = "Chittani Ramachandra Hegde",
            role = "Actor",
            description = "The first Yakshagana artist to be awarded the Padma Shri. Renowned for his fierce expressions and unparalleled energy in 'Chittani Style'.",
            vesha = "Rakshasa"
        ),
        Artist(
            id = "a2",
            name = "Sheni Gopalakrishna Bhat",
            role = "Actor",
            description = "A master of the spoken word (Arthadari). His intellectual debates on stage were legendary and drew crowds by the thousands.",
            vesha = "Punduvevesha"
        ),
        Artist(
            id = "a3",
            name = "G. Govinda Bhat",
            role = "Actor",
            description = "A master of the Tenku Thittu style, famous for his intellectual depth and precise dance movements in various roles.",
            vesha = "Punduvevesha"
        ),
        Artist(
            id = "a4",
            name = "Keremane Shivarama Hegde",
            role = "Actor",
            description = "Founder of the Idagunji Mandali, he brought Yakshagana to the international stage and was a master of powerful male roles.",
            vesha = "Bannada Vesha"
        ),
        Artist(
            id = "a5",
            name = "Kondadakuli Ramachandra",
            role = "Actor",
            description = "Famous for his majestic stage presence and exceptional dance skills in the Badagu Thittu style.",
            vesha = "Kiratha"
        ),

        // MADDALEGARA
        Artist(
            id = "m1",
            name = "Kadmalli Krishna",
            role = "Maddalegara",
            description = "A master of rhythm who elevated the Himmela to new heights with complex and resonant rhythmic patterns on the Maddale.",
            vesha = "Maddale"
        ),
        Artist(
            id = "m2",
            name = "Hiriyadka Gopal Rao",
            role = "Maddalegara",
            description = "A legendary percussionist whose command over the Maddale is considered a benchmark for the coastal art form.",
            vesha = "Maddale"
        ),
        Artist(
            id = "m3",
            name = "Nedle Narasimha Bhat",
            role = "Maddalegara",
            description = "Highly respected for his technical mastery and ability to perfectly complement the Bhagavatha's singing.",
            vesha = "Maddale"
        ),

        // HIMMELA
        Artist(
            id = "h1",
            name = "Sridhara Chande",
            role = "Himmela",
            description = "A virtuoso Chande player whose beats could bring a battle scene to life with thunderous intensity and speed.",
            vesha = "Chande"
        ),
        Artist(
            id = "h2",
            name = "Keshav Samaga",
            role = "Himmela",
            description = "A prominent Chande artist known for his rhythmic innovation and supporting role in many legendary performances.",
            vesha = "Chande"
        ),
        Artist(
            id = "h3",
            name = "Venkatesha Rao",
            role = "Himmela",
            description = "Dedicated artist providing vital rhythmic support to the entire performance for over four decades.",
            vesha = "Chande"
        )
    )
}

package com.example.vibees.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CollapsableLazyColumn(
            sections = listOf(
                CollapsableSection(
                    title = "About Us",
                    rows = listOf("Welcome to Vibees, the ultimate party experience app! We are a team of enthusiastic university undergraduates who are passionate about revolutionizing the way parties are hosted and enjoyed. Our goal is to create a platform that empowers individuals to craft personalized and unforgettable events, while prioritizing safety and inclusivity.\n" +
                            "\n" +
                            "At Vibees, we understand the challenges and concerns that come with hosting and attending parties. That's why we developed a comprehensive mobile app that combines innovative features with seamless functionality. With Vibees, you can create, customize, and manage your parties effortlessly, while connecting with like-minded individuals who share your idea of fun.\n" +
                            "\n" +
                            "We are committed to constantly improving our app and incorporating valuable feedback from our users. Your safety, enjoyment, and satisfaction are our top priorities. Join us in transforming the party experience and let's create unforgettable memories together!")
                ),
                CollapsableSection(
                    title = "FAQ",
                    rows = listOf("Q: How does Vibees work?\n" +
                            "A: Vibees is a mobile app that allows you to create and customize parties based on your unique vision. You can manage guest lists, process payments, and browse through a wide array of events that match your interests, budget, and schedule. Our standout feature is the generation of unique QR codes, which revolutionizes the way you authenticate and invite guests to your events.",
                        "Q: What if I have specific dietary restrictions or allergies?\n" +
                                "A: When creating or customizing your party, you can specify any dietary restrictions or allergies to ensure a safe and inclusive environment. Hosts can provide detailed information about the party supplies and activities, allowing guests to make informed decisions before attending.",
                        "Q: Can I cancel my attendance to a party?\n" +
                                "A: Yes, you can cancel your attendance to a party at any time through the Vibees app. However, please note that cancellation policies may vary depending on the specific party or event. We recommend reviewing the party details and contacting the host for any specific inquiries.")
                ),
                CollapsableSection(
                    title = "Privacy Policy",
                    rows = listOf("At Vibees, we value your privacy and are committed to protecting your personal information. We understand the concerns raised regarding safety and the potential harm that may arise from the use of our app. We have implemented measures to address these concerns and ensure a safe and secure environment for all users. Please take a moment to review our privacy policy:\n" +
                            "\n" +
                            "    Safety Measures:\n" +
                            "    We are actively working on incorporating safety features to minimize harm to individuals in and around parties. This includes providing warnings to non-partygoers in the vicinity of an event and implementing safeguards to prevent certain populations, such as minors, from accessing age-restricted content.\n" +
                            "\n" +
                            "    Verification Process:\n" +
                            "    To enhance safety and protect minors, we are implementing a verification process that requires valid government ID to confirm a user's identity and age. This process ensures that appropriate age restrictions are upheld, preventing underage individuals from accessing parties that involve alcohol or other age-restricted substances.\n" +
                            "\n" +
                            "    QR Code Authentication:\n" +
                            "    Our app utilizes QR codes for guest authentication. To prevent misuse, we are working on implementing measures to ensure that a QR code can only be scanned once, effectively tracking who is attending the party and reducing the possibility of unauthorized access.\n" +
                            "\n" +
                            "    Communication and Safety Alerts:\n" +
                            "    We are developing features that allow hosts to send alerts, warnings, or messages to all guests before and during the party. This ensures timely communication in case of any emergencies or changes to the event.\n" +
                            "\n" +
                            "    Privacy and Data Protection:\n" +
                            "    We adhere to strict data protection policies to safeguard your personal information. We collect only necessary data for the functioning of the app and will never share your information with third parties without your explicit consent.\n\n" +
                            "We appreciate your trust in us and are committed to continuously improving Vibees to provide a safe, enjoyable, and inclusive party experience for all users. Your feedback is invaluable to us, so please feel free to reach out to us with any questions, concerns, or suggestions." +
                            "\n\n\n\n")
                ),
            ),
        )
    }
}
@Composable
fun CollapsableLazyColumn(
    sections: List<CollapsableSection>,
    modifier: Modifier = Modifier
) {
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }
    LazyColumn(modifier) {
        sections.forEachIndexed { i, dataItem ->
            val collapsed = collapsedState[i]
            item(key = "header_$i") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            collapsedState[i] = !collapsed
                        }
                ) {
                    Icon(
                        Icons.Default.run {
                            if (collapsed)
                                KeyboardArrowDown
                            else
                                KeyboardArrowUp
                        },
                        contentDescription = "",
                        tint = Color.LightGray,
                    )
                    Text(
                        dataItem.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .weight(1f)
                    )
                }
                Divider()
            }
            if (!collapsed) {
                items(dataItem.rows) { row ->
                    Row {
                        Spacer(modifier = Modifier.size(MaterialIconDimension.dp))
                        Text(
                            row,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

data class CollapsableSection(val title: String, val rows: List<String>)

const val MaterialIconDimension = 24f


package com.example.feederku.views.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feederku.ui.theme.*
import com.example.feederku.views.donation.model.DonationCampaign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationRegistrationScreen(
    viewModel: DonationViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    
    // Form State
    var foundationName by remember { mutableStateOf("") }
    var notarialDeed by remember { mutableStateOf("") }
    var decreeNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    var campaignTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rescue") }
    var description by remember { mutableStateOf("") }
    
    var goal by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Donation Campaigns", fontWeight = FontWeight.Bold)
                        Text("Register", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = headerFooter,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(color = ColorFogCream, modifier = Modifier.padding(20.dp)) {
                if (currentStep < 4) {
                    Button(
                        onClick = { currentStep++ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column {
                        Button(
                            onClick = {
                                val newCampaign = DonationCampaign(
                                    title = campaignTitle,
                                    organizationName = foundationName,
                                    description = description,
                                    targetAmount = goal.toLongOrNull() ?: 0L,
                                    category = category,
                                    isVerified = false, // Team will verify
                                    daysRemaining = 30 // Default
                                )
                                viewModel.registerCampaign(newCampaign, onSuccess)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonGreen),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Submit a campaign", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Back", color = textBrown)
                        }
                    }
                }
            }
        },
        containerColor = ColorFogCream
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                StepIndicator(currentStep)
                Spacer(Modifier.height(32.dp))
            }

            when (currentStep) {
                1 -> item { FoundationVerificationStep(foundationName, {foundationName = it}, notarialDeed, {notarialDeed = it}, decreeNumber, {decreeNumber = it}, address, {address = it}, phone, {phone = it}) }
                2 -> item { CampaignInfoStep(campaignTitle, {campaignTitle = it}, category, {category = it}, description, {description = it}) }
                3 -> item { GoalAccountStep(goal, {goal = it}, bankAccount, {bankAccount = it}) }
                4 -> item { ReviewStep(foundationName, notarialDeed, decreeNumber, campaignTitle, goal, bankAccount) }
            }
            
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            StepCircle(step = i, isActive = i <= currentStep)
            if (i < 4) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(if (i < currentStep) buttonGreen else Color.LightGray))
            }
        }
    }
}

@Composable
fun StepCircle(step: Int, isActive: Boolean) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = if (isActive) buttonGreen else Color(0xFFE0E0E0),
        border = if (isActive) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = step.toString(),
                color = if (isActive) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FoundationVerificationStep(name: String, onNameChange: (String) -> Unit, deed: String, onDeedChange: (String) -> Unit, decree: String, onDecreeChange: (String) -> Unit, addr: String, onAddrChange: (String) -> Unit, ph: String, onPhChange: (String) -> Unit) {
    Column {
        Text("Foundation Verification", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textBrown)
        Text("Ensure your foundation is officially registered.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(Modifier.height(16.dp))
        
        Surface(color = ColorLinenWarm, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Why an official foundation?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Feederku only permits donation campaigns from legally registered foundations to protect donors and ensure funds are used for their intended purpose.", fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        RegInputField("Foundation Name", name, onNameChange)
        RegInputField("Notarial Deed Number", deed, onDeedChange)
        RegInputField("Ministry of Law and Human Rights Decree Number", decree, onDecreeChange)
        RegInputField("Foundation Address", addr, onAddrChange)
        RegInputField("Phone Number", ph, onPhChange)
    }
}

@Composable
fun CampaignInfoStep(title: String, onTitleChange: (String) -> Unit, selectedCat: String, onCatSelect: (String) -> Unit, desc: String, onDescChange: (String) -> Unit) {
    Column {
        Text("Campaign Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textBrown)
        Text("Clearly describe your campaign.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(Modifier.height(24.dp))
        RegInputField("Campaign Title", title, onTitleChange)
        
        Text("Category", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Medical", "Rescue", "Food", "Shelter")) { cat ->
                val isSel = selectedCat == cat
                Surface(
                    modifier = Modifier.clickable { onCatSelect(cat) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) buttonGreen else Color.White,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(cat, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = if (isSel) Color.White else Color.Gray, fontSize = 12.sp)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        RegInputField("Campaign Description", desc, onDescChange, isMultiline = true)
        
        Text("Cover Photo", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp).background(ColorLinenWarm, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, null, tint = buttonGreen)
                Text("Upload Cover Photo", fontWeight = FontWeight.Bold, color = textBrown)
                Text("Supported formats: JPG, PNG, MP4", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun GoalAccountStep(goal: String, onGoalChange: (String) -> Unit, account: String, onAccountChange: (String) -> Unit) {
    Column {
        Text("Goal & Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textBrown)
        Text("Set your goal and choose how to withdraw funds", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(Modifier.height(24.dp))
        RegInputField("Fundraising Goal", goal, onGoalChange)
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) { RegInputField("Start Date", "", {}) }
            Column(Modifier.weight(1f)) { RegInputField("End Date", "", {}) }
        }
        
        RegInputField("Foundation Account", account, onAccountChange)
    }
}

@Composable
fun ReviewStep(name: String, deed: String, decree: String, title: String, goal: String, account: String) {
    Column {
        Text("Review & Send", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textBrown)
        Text("Double-check before submitting.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(Modifier.height(24.dp))
        
        Surface(color = ColorLinenWarm, shape = RoundedCornerShape(24.dp), shadowElevation = 2.dp) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Foundation Profile", fontWeight = FontWeight.Bold, color = textBrown, modifier = Modifier.weight(1f))
                    Surface(color = buttonGreen, shape = RoundedCornerShape(8.dp)) {
                        Text("Verified Foundation", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                ReviewRow("Name", name, "No. Notarial Deed", deed)
                ReviewRow("No. SK", decree, "Email", "foundation@mail.com")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Surface(color = ColorLinenWarm, shape = RoundedCornerShape(24.dp), shadowElevation = 2.dp) {
            Column(Modifier.padding(20.dp).fillMaxWidth()) {
                Text("Campaign summary", fontWeight = FontWeight.Bold, color = textBrown)
                Spacer(Modifier.height(12.dp))
                ReviewRow("Funding target", "Rp $goal", "Bank", "BCA - 123456")
                ReviewRow("Start", "01/01/2024", "End", "31/01/2024")
                Text("Description", fontSize = 10.sp, color = Color.Gray)
                Text(title, fontSize = 14.sp)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = false, onCheckedChange = {})
            Text("I declare that all information provided is true & I agree to Feederku's Terms & Conditions and Fundraising Policy", fontSize = 10.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
fun ReviewRow(l1: String, v1: String, l2: String, v2: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.weight(1f)) {
            Text(l1, fontSize = 10.sp, color = Color.Gray)
            Text(v1, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f)) {
            Text(l2, fontSize = 10.sp, color = Color.Gray)
            Text(v2, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RegInputField(label: String, value: String, onValueChange: (String) -> Unit, isMultiline: Boolean = false) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(if (isMultiline) Modifier.height(100.dp) else Modifier),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = ColorLinenWarm,
                focusedContainerColor = ColorLinenWarm,
                unfocusedBorderColor = buttonGreen.copy(alpha = 0.3f),
                focusedBorderColor = buttonGreen
            )
        )
    }
}

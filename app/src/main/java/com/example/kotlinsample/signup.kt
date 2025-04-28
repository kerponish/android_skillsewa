package com.example.kotlinsample

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.kotlinsample.ui.theme.KotlinsampleTheme
import java.util.Calendar

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.ui.semantics.Role


class signup : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinsampleTheme {
                Scaffold { innerPadding ->
                    signup(innerPadding)

                }
            }
        }
    }
}

@Composable
fun signup(innerPaddingValues: PaddingValues) {
    var firstname by remember { mutableStateOf("") }
    var secondname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember{mutableStateOf("select option")}
    val options = listOf("Nepal", "Bhutan", "nigga")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
      val day      = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedDate by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        context,
        {_, selectedYear, selectedMonth , selectedDay ->
            selectedDate="$selectedDay/$selectedMonth/$selectedYear"
        },
        year,
        month,
        day
    )
    val radioOptions = listOf("Male", "Female", "Others")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    var rememberMe by remember{mutableStateOf(false)}


    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        )
        {
            Text(text = "Register")


        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = {Text("First Name")},
                placeholder = { Text("") },
                value = firstname,
                onValueChange = { firstname = it }


            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = {Text("Second Name")},
                placeholder = { Text("") },
                value = secondname,
                onValueChange = { secondname = it }

            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row (
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ){


            OutlinedTextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = {Text("Email")},
                placeholder = { Text("") },
                value = email,
                onValueChange = { email = it })
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Box(
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ){
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = {},
                    modifier = Modifier.
                    fillMaxWidth().
                    onGloballyPositioned{coordinates->
                        textFieldSize=coordinates.size.toSize()
                    }
                        .clickable{expanded=true},
                    placeholder = {Text("Select option")},
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )

                    }



                )
                DropdownMenu(
                    expanded=expanded,
                    onDismissRequest = {expanded=false},
                    modifier = Modifier.width(with(LocalDensity.current){textFieldSize.width.toDp()})

                ) {
                    options.forEach {
                        option->
                        DropdownMenuItem(
                            text={Text(option)},
                            onClick = {
                                selectedOptionText=option
                                expanded=false
                            })
                    }
                }

            }

        }
        Row {

            Box (
                modifier = Modifier.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { datePickerDialog.show()}){
                OutlinedTextField(
                    placeholder = {Text("Dob")},
                    modifier= Modifier.fillMaxWidth(),
                    value = selectedDate,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Gray,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black
                    ),
                    enabled = false,
                    onValueChange = {},
                )
            }
        }
        Row (modifier = Modifier.fillMaxWidth().width(400.dp).selectableGroup(),
        ) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = {
                    rememberMe= !rememberMe
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Green,
                    checkmarkColor = Color.White
                )
            )
            Text(text = "I have read, and accepted to the terms and conditions")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            ElevatedButton(
                onClick = {},
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
                    .padding(10.dp, 0.dp),
            )

            {
                Text(text = "Login")
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun myComp(){
    KotlinsampleTheme {
        signup(innerPaddingValues =  PaddingValues(0.dp))
    }
}



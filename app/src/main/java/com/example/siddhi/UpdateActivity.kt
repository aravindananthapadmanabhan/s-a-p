package com.example.siddhi

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlin.random.Random

data class Resource(
    val id: String,
    val name: String,
    val type: String, // "object" or "container"
    val isBaseContainer: Boolean,
    val resourceType: String, // "virtual" or "physical"
    val location: String,
    val parentContainer: String
)

private fun validateAndToast(resource: Resource, context: AppCompatActivity): Boolean {
    var retVal = true
    // Validation same as TSX
    if (resource.id.isEmpty()) {
        Toast.makeText(context, "Resource ID is required", Toast.LENGTH_SHORT).show()
        retVal =  false
    }
    if (resource.name.isEmpty() || resource.type.isEmpty() || resource.resourceType.isEmpty()) {
        Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        retVal =  false
    }
    if (resource.type == "container" && resource.isBaseContainer && resource.location.isEmpty()) {
        Toast.makeText(context, "Location is required for base containers", Toast.LENGTH_SHORT).show()
        retVal =  false
    }
    if (resource.type == "container" && !resource.isBaseContainer && resource.parentContainer.isEmpty()) {
        Toast.makeText(context, "Parent container is required for non-base containers", Toast.LENGTH_SHORT).show()
        retVal =  false
    }
    
    return retVal
}



class UpdateActivity : AppCompatActivity() {
    // Mock existing resources (in real app, fetch from DB or API)
    private val existingResources = mutableListOf(
        Resource("RES001", "Shelf A", "container", true, "physical", "Floor 1", ""),
        Resource("RES002", "Box 1", "container", false, "physical", "", "RES001"),
        Resource("RES003", "E-Book", "object", false, "virtual", "", "RES002")
    )

    private lateinit var etResourceId: EditText
    private lateinit var btnLookup: Button
    private lateinit var spinnerType: Spinner
    private lateinit var llIsBase: LinearLayout
    private lateinit var switchIsBase: Switch
    private lateinit var rgResourceType: RadioGroup
    private lateinit var etLocation: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvStatus: TextView
    private lateinit var btnScan: Button
    private lateinit var btnReset: Button
    private lateinit var etName: EditText
    private lateinit var spinnerParent: Spinner
    private lateinit var rbVirtual: RadioButton
    private lateinit var rbPhysical: RadioButton

    private val types = listOf("", "object", "container")
    private var isExistingResource = false


    private val defaultScanOptions = ScanOptions().apply {
        setPrompt("Scan a barcode")
        setBeepEnabled(true)
        setOrientationLocked(true)
        captureActivity = com.journeyapps.barcodescanner.CaptureActivity::class.java
    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract() // This is part of the IntentIntegrator library (e.g., zxing-android-embedded)
    ) { result ->
        if (result.contents != null) {
            val scanned = result.contents
            etResourceId.setText(scanned)
            Toast.makeText(this, "Barcode scanned: $scanned", Toast.LENGTH_SHORT).show()
            // Immediately perform the lookup after a successful scan
            btnLookup.performClick()
        } else {
            Toast.makeText(this, "No barcode captured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViews() {
        etResourceId = findViewById(R.id.et_resource_id)
        btnLookup = findViewById(R.id.btn_lookup)
        btnScan = findViewById(R.id.btn_scan)
        etName = findViewById(R.id.et_name)
        spinnerType = findViewById(R.id.spinner_type)
        llIsBase = findViewById(R.id.ll_is_base)
        switchIsBase = findViewById(R.id.switch_is_base)
        rgResourceType = findViewById(R.id.rg_resource_type)
        rbVirtual = findViewById(R.id.rb_virtual)
        rbPhysical = findViewById(R.id.rb_physical)
        etLocation = findViewById(R.id.et_location)
        spinnerParent = findViewById(R.id.spinner_parent)
        btnReset = findViewById(R.id.btn_reset)
        btnSubmit = findViewById(R.id.btn_submit)
        tvStatus  = findViewById(R.id.tv_status)
    }

    // Setup parent spinner (containers only)
    fun refreshParentSpinner(context: AppCompatActivity) {
        // Setup type spinner
        val typeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        val containers = existingResources.filter { it.type == "container" }
        val items = mutableListOf("")
        items.addAll(containers.map { "${it.id} - ${it.name}" })
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerParent.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        // Find views
        setupViews()

        // refresh spinners
        refreshParentSpinner(this)

        // hardcoded for now
        var isExistingResource = false

        fun populateFromResource(r: Resource) {
            etResourceId.setText(r.id)
            etName.setText(r.name)
            spinnerType.setSelection(types.indexOf(r.type).coerceAtLeast(0))
            switchIsBase.isChecked = r.isBaseContainer
            if (r.resourceType == "virtual") {
                rgResourceType.check(R.id.rb_virtual)
            } else {
                rgResourceType.check(R.id.rb_physical)
            }
            etLocation.setText(r.location)
            // select parent
            if (r.parentContainer.isNotEmpty()) {
                val containers = existingResources.filter { it.type == "container" }
                val index = containers.indexOfFirst { it.id == r.parentContainer }
                if (index >= 0) spinnerParent.setSelection(index + 1)
            }
            isExistingResource = true
            tvStatus.visibility = View.VISIBLE
            tvStatus.text = "Editing existing resource: ${r.id}"
            btnSubmit.text = "Update Resource"
        }

        fun resetForm(resourceIdReset: Boolean = true,setVisibility: Int = View.GONE) {
            if (resourceIdReset){
                etResourceId.setText("")
            }
            etName.setText("")
            spinnerType.setSelection(0)
            switchIsBase.isChecked = false
            rgResourceType.clearCheck()
            etLocation.setText("")
            spinnerParent.setSelection(0)
            isExistingResource = false
            tvStatus.visibility = setVisibility
            btnSubmit.text = "Create Resource"
        }

        fun setLocationParentVisibilityUpdate(locationVisibility: Int , parentVisibility: Int) {
            etLocation.visibility = locationVisibility
            spinnerParent.visibility = parentVisibility
        }

        fun setFormOnClickListener() {
            btnLookup.setOnClickListener {
                val id = etResourceId.text.toString().trim()
                if (id.isEmpty()) {
                    Toast.makeText(this, "Rsrc ID is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val found = existingResources.find { it.id == id }
                if (found != null) {
                    populateFromResource(found)
                    Toast.makeText(this, "Resource found: ${found.name}", Toast.LENGTH_SHORT).show()
                } else {
                    resetForm(resourceIdReset = false, setVisibility = View.VISIBLE)
                    tvStatus.text = "Creating new resource: $id"
                    Toast.makeText(this,"New Rsrc - please fill details",Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun handleZXingNotAvailable(e: ActivityNotFoundException) {
            btnScan.isEnabled = false
            btnScan.text = "Scanning..."
            btnScan.postDelayed({
                Log.e("ScanError", "ZXing not available: ${e.message}")
                val mockBarcode = "RES" + Random.nextInt(0, MCK_BC_LEVEL).toString().padStart(MCK_BC_PDD, '0')
                etResourceId.setText(mockBarcode)
                btnScan.isEnabled = true
                btnScan.text = "Scan"
                Toast.makeText(this, "Barcode scanned: $mockBarcode", Toast.LENGTH_SHORT).show()
                btnLookup.performClick()
            }, NO_BARCODE_DELAY)
        }

        fun setSpinnerOnItemSelectedListener() {
            spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = types.getOrNull(position) ?: ""
                    llIsBase.visibility = if (selected == "container") View.VISIBLE else View.GONE

                    // Show location if container & base
                    val isBase = switchIsBase.isChecked
                    if (selected == "container" && isBase) {
                        setLocationParentVisibilityUpdate(View.VISIBLE, View.GONE)
                    } else if (selected == "object" || (selected == "container" && !isBase)) {
                        setLocationParentVisibilityUpdate(View.GONE, View.VISIBLE)
                    } else {
                        setLocationParentVisibilityUpdate(View.GONE, View.GONE)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("SearchActivity", "Nothing is selected")
                }
            }   
        
        }

        fun setIsBaseOnCheckedListener() {
            switchIsBase.setOnCheckedChangeListener { _, isChecked ->
                val selected = types.getOrNull(spinnerType.selectedItemPosition) ?: ""
                if (selected == "container" && isChecked) {
                    setLocationParentVisibilityUpdate(View.VISIBLE, View.GONE)
                } else if (selected == "container" && !isChecked) {
                    setLocationParentVisibilityUpdate(View.GONE, View.VISIBLE)
                }
            }
        }

        fun setSubmitButtonOnClickListener() {
            btnSubmit.setOnClickListener {
                val id = etResourceId.text.toString().trim()
                val name = etName.text.toString().trim()
                val type = types.getOrNull(spinnerType.selectedItemPosition) ?: ""
                val resourceType = if (rbVirtual.isChecked) "virtual" else if (rbPhysical.isChecked) "physical" else ""
                val isBase = switchIsBase.isChecked
                val location = etLocation.text.toString().trim()
                val parent = when (spinnerParent.selectedItemPosition) {
                    0 -> ""
                    else -> {
                        val item = spinnerParent.selectedItem as String
                        item.split(" - ")[0]
                    }
                }

                
                if (!validateAndToast(Resource(
                    id,
                    name,
                    type,
                    isBase,
                    resourceType,
                    location,
                    parent),
                    this
                )) {
                    // The validation function already showed the Toast
                    return@setOnClickListener
                }

                if (isExistingResource) {
                    // Update the mock resource
                    val idx = existingResources.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        existingResources[idx] = Resource(
                            id,
                            name,
                            type,
                            isBase,
                            resourceType,
                            location,
                            parent
                        )
                    }
                    Toast.makeText(this, "Resource updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateActivity", "Updated resource: $id, $name")
                } else {
                    existingResources.add(
                        Resource(id, name, type, isBase, resourceType, location, parent)
                    )
                    refreshParentSpinner(this)
                    Toast.makeText(this, "Resource created successfully", Toast.LENGTH_SHORT).show()
                    Log.d("UpdateActivity", "Created resource: $id, $name")
                }

                // Optionally close activity
                // finish()
            }        
        }

        // Launch real barcode scanner via ZXing
        btnScan.setOnClickListener {
            try {
                barcodeLauncher.launch(defaultScanOptions)
            } catch (e: ActivityNotFoundException) {
                // If ZXing not available for some reason, fallback to simulated scan
                handleZXingNotAvailable(e)
            }
        }

        // Type selection shows/hides isBase and other fields
        setSpinnerOnItemSelectedListener()

        setIsBaseOnCheckedListener()

        btnReset.setOnClickListener { resetForm() }

        setSubmitButtonOnClickListener()


    }

    companion object {
        private const val NO_BARCODE_DELAY = 1400L
        private const val MCK_BC_LEVEL = 10000
        private const val MCK_BC_PDD = 4
    }
}

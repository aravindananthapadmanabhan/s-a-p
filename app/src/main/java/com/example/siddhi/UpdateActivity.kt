package com.example.siddhi

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlin.random.Random
import android.util.Log

data class Resource(
    val id: String,
    val name: String,
    val type: String, // "object" or "container"
    val isBaseContainer: Boolean,
    val resourceType: String, // "virtual" or "physical"
    val location: String,
    val parentContainer: String
)

class UpdateActivity : AppCompatActivity() {
    // Mock existing resources (in real app, fetch from DB or API)
    private val existingResources = mutableListOf(
        Resource("RES001", "Shelf A", "container", true, "physical", "Floor 1", ""),
        Resource("RES002", "Box 1", "container", false, "physical", "", "RES001"),
        Resource("RES003", "E-Book", "object", false, "virtual", "", "RES002")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        // Find views
        val etResourceId = findViewById<EditText>(R.id.et_resource_id)
        val btnScan = findViewById<Button>(R.id.btn_scan)
        val btnLookup = findViewById<Button>(R.id.btn_lookup)
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val etName = findViewById<EditText>(R.id.et_name)
        val spinnerType = findViewById<Spinner>(R.id.spinner_type)
        val llIsBase = findViewById<LinearLayout>(R.id.ll_is_base)
        val switchIsBase = findViewById<Switch>(R.id.switch_is_base)
        val rgResourceType = findViewById<RadioGroup>(R.id.rg_resource_type)
        val rbVirtual = findViewById<RadioButton>(R.id.rb_virtual)
        val rbPhysical = findViewById<RadioButton>(R.id.rb_physical)
        val etLocation = findViewById<EditText>(R.id.et_location)
        val spinnerParent = findViewById<Spinner>(R.id.spinner_parent)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        val btnReset = findViewById<Button>(R.id.btn_reset)

        // Register ZXing scanner result handler (after views are available)
        val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val scanned = result.contents
                etResourceId.setText(scanned)
                Toast.makeText(this, "Barcode scanned: $scanned", Toast.LENGTH_SHORT).show()
                btnLookup.performClick()
            } else {
                Toast.makeText(this, "No barcode captured", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup type spinner
        val types = listOf("", "object", "container")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        // Setup parent spinner (containers only)
        fun refreshParentSpinner() {
            val containers = existingResources.filter { it.type == "container" }
            val items = mutableListOf("")
            items.addAll(containers.map { "${it.id} - ${it.name}" })
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParent.adapter = adapter
        }

        refreshParentSpinner()

        var isExistingResource = false

        fun populateFromResource(r: Resource) {
            etResourceId.setText(r.id)
            etName.setText(r.name)
            spinnerType.setSelection(types.indexOf(r.type).coerceAtLeast(0))
            switchIsBase.isChecked = r.isBaseContainer
            if (r.resourceType == "virtual") rgResourceType.check(R.id.rb_virtual) else rgResourceType.check(R.id.rb_physical)
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

        fun resetForm() {
            etResourceId.setText("")
            etName.setText("")
            spinnerType.setSelection(0)
            switchIsBase.isChecked = false
            rgResourceType.clearCheck()
            etLocation.setText("")
            spinnerParent.setSelection(0)
            isExistingResource = false
            tvStatus.visibility = View.GONE
            btnSubmit.text = "Create Resource"
        }

        // Lookup
        btnLookup.setOnClickListener {
            val id = etResourceId.text.toString().trim()
            if (id.isEmpty()) {
                Toast.makeText(this, "Resource ID is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val found = existingResources.find { it.id == id }
            if (found != null) {
                populateFromResource(found)
                Toast.makeText(this, "Resource found: ${found.name}", Toast.LENGTH_SHORT).show()
            } else {
                // New resource - reset other fields but keep id
                etName.setText("")
                spinnerType.setSelection(0)
                switchIsBase.isChecked = false
                rgResourceType.clearCheck()
                etLocation.setText("")
                spinnerParent.setSelection(0)
                isExistingResource = false
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = "Creating new resource: $id"
                Toast.makeText(this, "New resource - please fill in the details", Toast.LENGTH_SHORT).show()
            }
        }

        // Launch real barcode scanner via ZXing
        btnScan.setOnClickListener {
            try {
                val options = ScanOptions()
                options.setPrompt("Scan a barcode")
                options.setBeepEnabled(true)
                options.setOrientationLocked(true)
                options.captureActivity = com.journeyapps.barcodescanner.CaptureActivity::class.java
                barcodeLauncher.launch(options)
            } catch (e: Exception) {
                // If ZXing not available for some reason, fallback to simulated scan
                btnScan.isEnabled = false
                btnScan.text = "Scanning..."
                btnScan.postDelayed({
                    val mockBarcode = "RES" + Random.nextInt(0, 10000).toString().padStart(4, '0')
                    etResourceId.setText(mockBarcode)
                    btnScan.isEnabled = true
                    btnScan.text = "Scan"
                    Toast.makeText(this, "Barcode scanned: $mockBarcode", Toast.LENGTH_SHORT).show()
                    btnLookup.performClick()
                }, 1400)
            }
        }

        // Type selection shows/hides isBase and other fields
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = types.getOrNull(position) ?: ""
                if (selected == "container") {
                    llIsBase.visibility = View.VISIBLE
                } else {
                    llIsBase.visibility = View.GONE
                }

                // Show location if container & base
                val isBase = switchIsBase.isChecked
                if (selected == "container" && isBase) {
                    etLocation.visibility = View.VISIBLE
                    spinnerParent.visibility = View.GONE
                } else if (selected == "object" || (selected == "container" && !isBase)) {
                    etLocation.visibility = View.GONE
                    spinnerParent.visibility = View.VISIBLE
                } else {
                    etLocation.visibility = View.GONE
                    spinnerParent.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        switchIsBase.setOnCheckedChangeListener { _, isChecked ->
            val selected = types.getOrNull(spinnerType.selectedItemPosition) ?: ""
            if (selected == "container" && isChecked) {
                etLocation.visibility = View.VISIBLE
                spinnerParent.visibility = View.GONE
            } else if (selected == "container" && !isChecked) {
                etLocation.visibility = View.GONE
                spinnerParent.visibility = View.VISIBLE
            }
        }

        btnReset.setOnClickListener { resetForm() }

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

            // Validation same as TSX
            if (id.isEmpty()) { Toast.makeText(this, "Resource ID is required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (name.isEmpty() || type.isEmpty() || resourceType.isEmpty()) { Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (type == "container" && isBase && location.isEmpty()) { Toast.makeText(this, "Location is required for base containers", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (type == "container" && !isBase && parent.isEmpty()) { Toast.makeText(this, "Parent container is required for non-base containers", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            if (isExistingResource) {
                // Update the mock resource
                val idx = existingResources.indexOfFirst { it.id == id }
                if (idx >= 0) {
                    existingResources[idx] = Resource(id, name, type, isBase, resourceType, location, parent)
                }
                Toast.makeText(this, "Resource updated successfully", Toast.LENGTH_SHORT).show()
                Log.d("UpdateActivity", "Updated resource: $id, $name")
            } else {
                existingResources.add(Resource(id, name, type, isBase, resourceType, location, parent))
                refreshParentSpinner()
                Toast.makeText(this, "Resource created successfully", Toast.LENGTH_SHORT).show()
                Log.d("UpdateActivity", "Created resource: $id, $name")
            }

            // Optionally close activity
            // finish()
        }

    }
}

package com.example.workmanager

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.workmanager.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var bindingMainActivity : ActivityMainBinding
    private var permissionRequestCount: Int = 0
    private val permissionsToAskFor = ArrayList<String>()
    private val userInterfacesToEdit = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainActivity = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        preparePermissionStageAndUIComponents()
        callForNecessaryPermissions()
    }

    private fun preparePermissionStageAndUIComponents(){
        permissionsToAskFor.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsToAskFor.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // For induvidual Manipulation
        userInterfacesToEdit.add(bindingMainActivity.ivLocation)
        userInterfacesToEdit.add(bindingMainActivity.ivImage)
    }

    private fun callForNecessaryPermissions(){
        val areAllPermissionsGranted = checkPermissions()
        if(!areAllPermissionsGranted){
            if(permissionRequestCount < AppConstants.MAXIMUM_PERMISSIONS_COUNT){
                permissionRequestCount++
                ActivityCompat.requestPermissions(
                    this,
                    permissionsToAskFor.toTypedArray(),
                    AppConstants.PERMISSIONS_SET
                )
            }else{
                var snackBarMessage = ""
                if(checkSelfPermission(permissionsToAskFor[0]) == PackageManager.PERMISSION_DENIED) {
                    bindingMainActivity.ivLocation.setBackgroundResource(R.drawable.disabled_gradient)
                    snackBarMessage = getString(R.string.set_permission_location_in_settings)
                }
                if(checkSelfPermission(permissionsToAskFor[1]) == PackageManager.PERMISSION_DENIED) {
                    bindingMainActivity.ivImage.setBackgroundResource(R.drawable.disabled_gradient)
                    snackBarMessage = getString(R.string.set_permission_storage_in_settings)
                }
                Snackbar.make(
                    findViewById(R.id.clMain),
                    snackBarMessage,
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions were granted after a permissions request flow.
        // Recursive Code for 'n' number of times set
        if (requestCode == AppConstants.PERMISSIONS_SET) {
            callForNecessaryPermissions()
        }

        // This Code is to handle the Views and Results separately
        /*if(requestCode == AppConstants.PERMISSIONS_SET){
            for(i in permissions.indices){
                if(permissions[i] == permissionsToAskFor[i]
                    && grantResults[i] == PackageManager.PERMISSION_DENIED){
                    userInterfacesToEdit[i].setBackgroundResource(R.drawable.disabled_gradient)
                }
            }
        }*/
    }


    private fun checkPermissions():Boolean{
        var hasPermission = true
        for(permission in permissionsToAskFor){
            hasPermission = hasPermission and (ContextCompat.checkSelfPermission(this,
            permission) == PackageManager.PERMISSION_GRANTED)
        }
        return hasPermission
    }

}
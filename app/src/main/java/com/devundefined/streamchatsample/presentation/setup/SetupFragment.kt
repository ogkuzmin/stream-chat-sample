package com.devundefined.streamchatsample.presentation.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentSetupBinding
import com.devundefined.streamchatsample.infrastructure.KeyValueStorage
import com.devundefined.streamchatsample.infrastructure.Toggles
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class SetupFragment : BottomSheetDialogFragment() {

    private val keyValueStorage: KeyValueStorage by inject()
    private lateinit var binding: FragmentSetupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
            .also { binding = FragmentSetupBinding.bind(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            switcher.isChecked = keyValueStorage.get(Toggles.KEY_LIVEDATA_TOGGLE, false)
            saveButton.setOnClickListener {
                if (keyValueStorage.get(Toggles.KEY_LIVEDATA_TOGGLE, false) != switcher.isChecked) {
                    keyValueStorage.save(Toggles.KEY_LIVEDATA_TOGGLE, switcher.isChecked)
                    dismissAllowingStateLoss()
                    activity?.recreate()
                } else {
                    dismiss()
                }
            }
        }
    }
}
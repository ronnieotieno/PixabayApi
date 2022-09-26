package com.ronnie.presenatation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.bold
import androidx.fragment.app.DialogFragment
import com.ronnie.presenatation.R
import com.ronnie.presenatation.databinding.FragmentConfirmDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//Irrelevant
@AndroidEntryPoint
class ConfirmDialogFragment @Inject constructor() :
    DialogFragment(R.layout.fragment_confirm_dialog) {

    private lateinit var binding: FragmentConfirmDialogBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentConfirmDialogBinding.bind(view)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogTheme

        val user = arguments?.getString("user")

        //Shows the name of the user in bold
        val alert = SpannableStringBuilder()
            .append(getString(R.string.alert))
            .append(" ")
            .bold { append(user) }
            .append("?")

        binding.apply {
            caution.text = alert
            cancel.setOnClickListener {
                dialog?.dismiss()
            }
            confirm.setOnClickListener {
               // (parentFragment as ImagesListFragment).navigate()
                dialog?.dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}
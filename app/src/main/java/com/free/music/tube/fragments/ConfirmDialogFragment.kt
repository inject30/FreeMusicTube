package com.free.music.tube.fragments

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ConfirmDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = checkNotNull(arguments) {
            "Instances of ConfirmDialogFragment should be created using the newInstance method."
        }

        val builder = AlertDialog.Builder(context!!).setTitle(args.getString(ARG_TITLE))

        val message = args.getString(ARG_MESSAGE)
        builder.setMessage(message)

        val positive = args.getInt(ARG_POSITIVE)
        if (positive != 0) {
            builder.setPositiveButton(positive, this)
        }

        val negative = args.getInt(ARG_NEGATIVE)
        if (negative != 0) {
            builder.setNegativeButton(negative, this)
        }

        val neutral = args.getInt(ARG_NEUTRAL)
        if (neutral != 0) {
            builder.setNeutralButton(neutral, this)
        }

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
        super.onCancel(dialog)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        targetFragment?.onActivityResult(targetRequestCode, which, null)
    }

    companion object Factory {
        private const val ARG_TITLE = "dialog_title"
        private const val ARG_MESSAGE = "dialog_message"
        private const val ARG_POSITIVE = "dialog_positive_button"
        private const val ARG_NEGATIVE = "dialog_negative_button"
        private const val ARG_NEUTRAL = "dialog_neutral_button"

        /**
         * Create a new instance of this DialogFragment.
         *
         * @param caller the Fragment that displays this dialog
         * to which the result should be forwarded.
         * @param requestCode a number identifying the request that'll
         * be forwarded to [Fragment.onActivityResult] once interaction with the dialog is finished.
         * @param title the title of the dialog to display.
         * @param message an optional message to display as the dialog's body.
         * @param positiveButton an optional resource id of the text.
         * to display in the positive button. If 0, no positive button will be shown.
         * @param negativeButton an optional resource id of the text
         * to display in the negative button. If 0, no negative button will be shown.
         * @param neutralButton an optional resource id of the text
         * to display in the neutral button. If 0, no neutral button will be shown.
         */
        @JvmStatic
        fun newInstance(
            caller: Fragment?,
            requestCode: Int,
            title: String? = null,
            message: String? = null,
            @StringRes positiveButton: Int = 0,
            @StringRes negativeButton: Int = 0,
            @StringRes neutralButton: Int = 0

        ) = ConfirmDialogFragment().apply {
            setTargetFragment(caller, requestCode)
            arguments = Bundle(5).apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putInt(ARG_POSITIVE, positiveButton)
                putInt(ARG_NEGATIVE, negativeButton)
                putInt(ARG_NEUTRAL, neutralButton)
            }
        }
    }
}
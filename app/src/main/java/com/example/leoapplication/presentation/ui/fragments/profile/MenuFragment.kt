package com.example.leoapplication.presentation.ui.fragments.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MenuFragment : Fragment() {

    lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goProfile()
        binding.txtHelpMenu.setOnClickListener { showSupportDialog() }
        goIg()

        binding.txtCurrencyMenu.setOnClickListener {
            CurrencyDialogFragment().show(parentFragmentManager, "currencyDialog")
        }



    }

    private fun goProfile() {
        binding.txtAccountMenu.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_profileFragment)

        }
        binding.cardMain.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_nav_home)
        }
    }




        private fun saveLanguage(context: Context, language: String) {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("selected_language", language).apply()
        }

        private fun getSavedLanguage(context: Context): String {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return prefs.getString("selected_language", "az") ?: "az"
        }

        private fun updateLocale(context: Context, language: String) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        private fun applyLanguage(language: String) {
            saveLanguage(requireContext(), language)
            updateLocale(requireContext(), language)
            requireActivity().recreate()  // Activity restart ilə bütün app UI yenilənir
        }

        private fun showSupportDialog() {
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.dialog_support, null)
            dialog.setContentView(view)

            // Sosial media
            view.findViewById<LinearLayout>(R.id.layoutWhatsapp).setOnClickListener {
                openLink("https://wa.me/994123101488", "com.whatsapp"); dialog.dismiss()
            }
            view.findViewById<LinearLayout>(R.id.layoutTelegram).setOnClickListener {
                openLink("https://t.me/Leobank_bot"); dialog.dismiss()
            }
            view.findViewById<LinearLayout>(R.id.layoutFacebook).setOnClickListener {
                openLink("https://www.facebook.com/leobank.az/"); dialog.dismiss()
            }
            view.findViewById<LinearLayout>(R.id.layoutViber).setOnClickListener {
                openLink("https://www.viber.com/leobank.az/"); dialog.dismiss()
            }

            // Dil seçimi
            view.findViewById<ConstraintLayout>(R.id.layoutLanguage).setOnClickListener {
                dialog.dismiss()

                val dialogLanguage = BottomSheetDialog(requireContext())
                val viewLanguage = layoutInflater.inflate(R.layout.dialog_language, null)
                dialogLanguage.setContentView(viewLanguage)

                val radioGroup = viewLanguage.findViewById<RadioGroup>(R.id.radioGroupLanguage)
                val radioAzerbaijan = viewLanguage.findViewById<RadioButton>(R.id.radioAzerbaijan)
                val radioRussian = viewLanguage.findViewById<RadioButton>(R.id.radioRussian)

                // Seçilmiş dili göstər
                if (getSavedLanguage(requireContext()) == "az") radioGroup.check(R.id.radioAzerbaijan)
                else radioGroup.check(R.id.radioRussian)

                radioAzerbaijan.setOnClickListener {
                    radioGroup.check(R.id.radioAzerbaijan)
                    applyLanguage("az")
                    dialogLanguage.dismiss()
                }

                radioRussian.setOnClickListener {
                    radioGroup.check(R.id.radioRussian)
                    applyLanguage("ru")
                    dialogLanguage.dismiss()
                }

                dialogLanguage.show()
            }
            dialog.show()
        }


        private fun openLink(url: String, packageName: String? = null) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (packageName != null) intent.setPackage(packageName)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Tətbiq açılmadı", Toast.LENGTH_SHORT).show()
            }
        }


    private fun goIg(){
        binding.txtGMenu.setOnClickListener {
            val url = "https://www.instagram.com/leobank.az/"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Instagram aça bilmədik", Toast.LENGTH_SHORT).show()
            }
        }
    }


    }


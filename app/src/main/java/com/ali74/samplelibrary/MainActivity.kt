package com.ali74.samplelibrary


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.ali74.libkot.BindingActivity
import com.ali74.libkot.city.CityDialog
import com.ali74.libkot.city.CityParser
import com.ali74.libkot.patternBuilder.AboutDialog
import com.ali74.libkot.patternBuilder.MessageDialogBuilder
import com.ali74.libkot.patternBuilder.QuestionDialogBuilder
import com.ali74.libkot.patternBuilder.SnackBarBuilder
import com.ali74.libkot.slider.SlideShow
import com.ali74.libkot.slider.SliderBuilder
import com.ali74.libkot.ui.ProgressDialog
import com.ali74.samplelibrary.databinding.MainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BindingActivity<MainBinding>() {

    override fun getLayoutResId() = R.layout.activity_main

    private val description = "        لورم ایپسوم متن ساختگی با تولید سادگی نامفهوم از صنعت چاپ، و با استفاده از طراحان گرافیک است، چاپگرها و متون بلکه روزنامه و مجله در ستون و سطرآنچنان که لازم است، و برای شرایط فعلی تکنولوژی مورد نیاز، و کاربردهای متنوع با هدف بهبود ابزارهای کاربردی می باشد، کتابهای زیادی در شصت و سه درصد گذشته حال و آینده، شناخت فراوان جامعه و متخصصان را می طلبد، تا با نرم افزارها شناخت بیشتری را برای طراحان رایانه ای علی الخصوص طراحان خلاقی، و فرهنگ پیشرو در زبان فارسی ایجاد کرد، در این صورت می توان امید داشت که تمام و دشواری موجود در ارائه راهکارها، و شرایط سخت تایپ به پایان رسد و زمان مورد نیاز شامل حروفچینی دستاوردهای اصلی، و جوابگوی سوالات پیوسته اهل دنیای موجود طراحی اساسا مورد استفاده قرار گیرد.\n"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testSlider()

        binding.SimpleSnackBar.setOnClickListener {
            SnackBarBuilder("SimpleSnackBar").setNegativeMargin(0F).show(this@MainActivity)
        }
        binding.MarginSnackBar.setOnClickListener {
            SnackBarBuilder("Higher SnackBar").setNegativeMargin(-90F).show(this@MainActivity)
        }

        binding.AboutDialog.setOnClickListener { testAboutDialog()}


        binding.ProgressDialog.setOnClickListener { ProgressDialog(this).show() }


        City_Dialog.setOnClickListener { testCityDialog() }

        binding.AlertMessage.setOnClickListener { testAlertMessageDialog() }
        binding.QuestionDialog.setOnClickListener { testQuestionAlertDialog() }

    }



    private fun testSlider(){
        //sliderTester
        val img_one = "https://wl-brightside.cf.tsp.li/resize/728x/jpg/f2c/662/7fb5c25bc493f5189bf3c7b0df.jpg"
        val img_two = "https://s31242.pcdn.co/wp-content/uploads/2019/05/Personality-picture-test-1.jpg"
        val img_three = "https://www.test-english.com/img/test-english-home-Level-test.jpg"
        val models = mutableListOf<SlideShow>()
        models.add(SlideShow(img_one, 1, 2, "null" ))
        models.add(SlideShow(img_two, 2, 2, "salam"))
        models.add(SlideShow(img_three, 3, 2, "test"))

        SliderBuilder(this)
            .setModels(models)
            .setRecyclerView(findViewById(R.id.txtTest))
            .setItemWidth(300)
            .setAutoScroll(true)
            .setRadius(3f)
            .setTimer(3000)
            .useCarouselLayoutManager(true)
            .setOnclick(object : SliderBuilder.OnclickListener {
                override fun onSliderClicked(slider: SlideShow) {
                    Toast.makeText(this@MainActivity, "Slider Clicked", Toast.LENGTH_SHORT).show()
                }
            }).create()
    }


    private fun testAboutDialog(){

        AboutDialog(this@MainActivity)
            .setToolbarIcon(R.drawable.logo_rahkar)
            .setDescription(description)
            .setAddress("آدرس وارد شده")
            .setPhone("شماره تلفن")
            .setAppVersion("1.5")
            .setEmail("info@pdb-company.com")
            .create()
            .show()
    }


    private fun testCityDialog(){
        //city Tester
        val stream = this.assets.open("city.xml")
        val city = CityParser(stream).getDataList()
        val cityDialog = CityDialog(this@MainActivity, city, 2, "انتخاب کنید")
        cityDialog.setOnClickListener(object : CityDialog.SetOnItemClick {
            override fun setOnclick(title: String, itemSelected: Int) {
                SnackBarBuilder(title).setNegativeMargin(0F).show(this@MainActivity)
                cityDialog.dismiss()
            }
        })
        cityDialog.show()
    }


    private fun testAlertMessageDialog(){
        MessageDialogBuilder(this)
            .setMessage(description)
            .setIconToolbar(R.drawable.logo_rahkar , R.color.white)
            .setBtnConfirm("دکمه" , R.color.green)
            .setOnclickBtn { Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show() }
            .create()
            .show()
    }

    private fun testQuestionAlertDialog(){
        QuestionDialogBuilder(this)
            .setMessage(description)
            .setIconToolbar(R.drawable.logo_rahkar , R.color.white)
            .setBtnConfirm("قبول" , R.color.green)
            .setBtnCancel("کنسل", R.color.red)
            .setOnclickBtn({
                Toast.makeText(this, "Confirm Clicked", Toast.LENGTH_SHORT).show()
            } ,
                {
                    Toast.makeText(this, "Cancel Clicked", Toast.LENGTH_SHORT).show()
                }).create().show()
    }

}
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
            SnackBarBuilder("SimpleSnackBar").show(this@MainActivity)
        }
        binding.MarginSnackBar.setOnClickListener {
            SnackBarBuilder("Higher SnackBar").show(this@MainActivity)
        }

        binding.AboutDialog.setOnClickListener { testAboutDialog()}


        binding.ProgressDialog.setOnClickListener { ProgressDialog(this).show() }


        City_Dialog.setOnClickListener { testCityDialog() }

        binding.AlertMessage.setOnClickListener { testAlertMessageDialog() }
        binding.QuestionDialog.setOnClickListener { testQuestionAlertDialog() }

    }



    private fun testSlider(){
        //sliderTester
        val img_one = "https://obbo.ir/images/banner/1494c74a18274ff18bbef31bf9c2cb0e.jpg"
        val img_two = "https://obbo.ir/images/banner/bfb65f6afe7c4229ba7a68d3576b6c95.jpg"
        val img_three = "https://obbo.ir/images/banner/5fddd240aa8042d49f809e5271723047.jpg"
        val img_four = "https://obbo.ir/images/banner/788c3b42228343248224b4fa2d4de0d1.jpg"
        val models = mutableListOf<SlideShow>()
        models.add(SlideShow( 1,img_one, 1, 2, "null" , 5))
        models.add(SlideShow(2,img_two, 2, 2, "salam" , 5))
        models.add(SlideShow(3,img_three, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_four, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
       /* models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))
        models.add(SlideShow(4,img_two, 3, 2, "test" , 5))*/

        SliderBuilder(this)
            .setModels(models)
            .setRecyclerView(findViewById(R.id.txtTest))
            .setItemWidth(280)
            .setAutoScroll(true)
            .setTimer(3000)
            .setMarginItem(2)
            .setItemHeight(200)
            .setOnclick(object : SliderBuilder.OnclickListener {
                override fun onSliderClicked(slider: SlideShow) {
                    Toast.makeText(this@MainActivity, "${slider.id}", Toast.LENGTH_SHORT).show()
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
                SnackBarBuilder(title).show(this@MainActivity)
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
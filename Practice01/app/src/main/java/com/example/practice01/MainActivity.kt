package com.example.practice01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toolbar
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected

class MainActivity : AppCompatActivity() {
    lateinit var navHost: NavHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fcv: FragmentContainerView = findViewById(R.id.fragment) // 이런 형태로 NavHostFragment가 navController을 들고 있다. 근데, Navigation static class를 통해 간단하게 얻을수도 있다.
        navHost = fcv.findFragment<NavHostFragment>() // NavHostFragment가 NavHost를 implementation하기 때문에 가능

//        val button:Button = findViewById(R.id.gtsButton)
//        button.setOnClickListener {
//            val navController = Navigation.findNavController(it) //  인자로 들어간 view의 NavController을 찾아줌. NavController은 Activity당 하나씩 있음.?? -> Activity 여러개당 하나 할당 되는 것도 가능하다.
////            //val navController = navHost.navController
////            // NavHostFragment 당 하나임? 즉, first destination 당 하나인건가
////            // 하나의 nav_graph 당 하나의 first destination 밖에 없음. 따라서 하나의 nav_graph 당 하나의 NavController가 있음.
////            // 이러한 NavController는 해당 하는 어느 코드에서든 반환 받을 수 있다.
////
////            // 따라서 button click listener 에다가 action을 다는 것은 어디서에든 가능하다. -> 아니!!, inflate 안된 것은 id를 찾지 못하기때문에 button을 찾는 findViewById로부터 값을 반환을 못받는다.
////            // 잠깐 되는 것처럼 보인 이유는 공교롭게도 이 버튼을 갖고 있는 fragment가 FirstFragment 이기 때문이다. 즉, 버튼 설정은 각 Fragment에서 해줘야 옳다.
//            val options = NavOptions.Builder()
//                .setEnterAnim(R.anim.nav_default_enter_anim)
//                .setExitAnim(R.anim.nav_default_exit_anim)
//                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//                .setPopExitAnim(R.anim.nav_default_exit_anim)
//                .build()
//
//            navController.navigate(R.id.secondFragment, null, options)
//        }

//        findViewById<Button>(R.id.gtsButton).setOnClickListener(
//            Navigation.createNavigateOnClickListener(R.id.action_firstFragment_to_secondFragment)
//        )

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

//        val navController = findNavController(this, R.id.fragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)


    }


//    fun onClick(view: View) {
//        val navController = Navigation.findNavController(view)
//        navController.navigate(R.id.action_firstFragment_to_secondFragment)
//    }

    // 재미있는 사실 : Nav_graph상에 있는 activity의 경우 NavHostFragment위에 올라가는게 아니다. 별도로 activity가 start하는 것임. 따라서 action을 support 하지 않는다.
    // 즉, fragment->activity   는 가지만,  activity->fragment 는 이행할 수 없다. (컴파일 에러 발생)

}
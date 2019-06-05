package com.lambdaschool.dogsinitial

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@SpringBootApplication
open class DogsInitialApplication
{
    companion object
    {
        private lateinit var ourDogList: DogList

        @JvmStatic
        fun main(args: Array<String>)
        {
//            try
//            {


            ourDogList = DogList()
            val ctx: ApplicationContext = SpringApplication.run(DogsInitialApplication::class.java, *args)

            val dispatcherServlet: DispatcherServlet = ctx.getBean("dispatcherServlet") as DispatcherServlet
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
//            }
//            catch (e:Exception)
//            {
//                println(e)
//            }
        }

        fun getOurDogList(): DogList = ourDogList
    }
}
package com.lambdaschool.dogsinitial.controller

import com.lambdaschool.dogsinitial.CheckDog
import com.lambdaschool.dogsinitial.DogsInitialApplication
import com.lambdaschool.dogsinitial.exception.InvalidDataTypeException
import com.lambdaschool.dogsinitial.exception.ResourceNotFoundException
import com.lambdaschool.dogsinitial.model.Dog
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/dogs")
class DogController
{
    // localhost:2019/dogs/dogs
    @GetMapping(value = ["/dogs"], produces = ["application/json"])
    fun getAllDogs(): ResponseEntity<*>
    {
        val rtnDogs: MutableList<Dog> = DogsInitialApplication.getOurDogList().dogList

        if (rtnDogs.isEmpty())
        {
            throw ResourceNotFoundException("No results to display")
        }

        return ResponseEntity(rtnDogs, HttpStatus.OK)
    }

    // localhost:2019/dogs/{id}
    @GetMapping(value = ["/{id}"], produces = ["application/json"])
    fun getDogDetail(@PathVariable id: Long): ResponseEntity<*>
    {
        val rtnDog: Dog? = DogsInitialApplication.getOurDogList().findDog(CheckDog { d -> d.id == id })

        if (rtnDog == null)
        {
            throw ResourceNotFoundException("No dog exists with id $id")
        }

        return ResponseEntity<Dog>(rtnDog, HttpStatus.OK)
    }

    // localhost:2019/dogs/breeds/{breed}
    @GetMapping(value = ["/breeds/{breed}"], produces = ["application/json"])
    fun getDogBreeds(@PathVariable breed: String): ResponseEntity<*>
    {
        val rtnDogs: List<Dog> = DogsInitialApplication.getOurDogList().findDogs(CheckDog { d -> d.breed.toLowerCase().equals(breed.toLowerCase()) })

        if (rtnDogs.isEmpty())
        {
            throw ResourceNotFoundException("No dog breeds called $breed")
        }

        return ResponseEntity(rtnDogs, HttpStatus.OK)
    }

    //localhost:2019/dogs/dogtable
    @GetMapping(value = ["/dogtable"], produces = ["application/json"])
    fun displayDogTable(): ModelAndView
    {
        val dogList: MutableList<Dog> = DogsInitialApplication.getOurDogList().dogList
        dogList.sortBy { it.breed }

        val mav = ModelAndView()
        mav.viewName = "dogs"
        mav.addObject("dogList", dogList)

        return mav
    }

    //localhost:2019/dogs/suitabledogtable
    @GetMapping(value = ["/suitabledogtable"], produces = ["application/json"])
    fun displaySuitableDogTable(): ModelAndView
    {
        val dogList: List<Dog> = DogsInitialApplication.getOurDogList().dogList.filter { it.isApartmentSuitable }.sortedBy { a -> a.breed }

        val mav = ModelAndView()
        mav.viewName = "dogs"
        mav.addObject("dogList", dogList)

        return mav
    }
}
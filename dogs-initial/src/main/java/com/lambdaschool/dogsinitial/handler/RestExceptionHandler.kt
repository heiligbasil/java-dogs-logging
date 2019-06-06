package com.lambdaschool.dogsinitial.handler

import com.lambdaschool.dogsinitial.DogsInitialApplication
import com.lambdaschool.dogsinitial.controller.DogController
import com.lambdaschool.dogsinitial.exception.ResourceNotFoundException
import com.lambdaschool.dogsinitial.model.ErrorDetail
import com.lambdaschool.dogsinitial.model.MessageDetail
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler()
{
    @Autowired
    private val messageSource: MessageSource? = null

    @Autowired
    internal var rt: RabbitTemplate? = null

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(rnfe: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<*>
    {
        val messageLog: String = "$request on ${LocalDateTime.now()}"
        val message = MessageDetail(messageLog, 2, false)
        rt!!.convertAndSend(DogsInitialApplication.QUEUE_NAME_ERROR, message)

        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.NOT_FOUND.value()
        errorDetail.title = "Resource Not Found"
        errorDetail.detail = rnfe.message
        errorDetail.developerMessage = rnfe.javaClass.name

        return ResponseEntity<Any>(errorDetail, null, HttpStatus.NOT_FOUND)
    }

/*    @ExceptionHandler(InvalidDataTypeException::class)
    fun handleInvalidDataTypeException(idte: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<*>
    {
        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.NOT_FOUND.value()
        errorDetail.title = "Invalid DataType"
        errorDetail.detail = idte.message
        errorDetail.developerMessage = idte.javaClass.name

        return ResponseEntity<Any>(errorDetail, null, HttpStatus.BAD_REQUEST)
    }*/

/*
    override fun handleMissingPathVariable(ex: MissingPathVariableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any>
    {
        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.NOT_FOUND.value()
        errorDetail.title = "Missing Path Variable"
        errorDetail.detail = ex.message
        errorDetail.developerMessage = ex.javaClass.name

        return ResponseEntity<Any>(errorDetail, null, HttpStatus.CONFLICT)
    }
*/

/*    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFoundException(unfe: UrlNotFoundException, request: HttpServletRequest): ResponseEntity<*>
    {
        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.NOT_FOUND.value()
        errorDetail.title = "URL Not Found Exception"
        errorDetail.detail = unfe.message
        errorDetail.developerMessage = unfe.javaClass.name

        return ResponseEntity<Any>(errorDetail, null, HttpStatus.NOT_FOUND)
    }*/

    override fun handleTypeMismatch(ex: TypeMismatchException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any>
    {
        val messageLog: String = "$status on ${LocalDateTime.now()}"
        val message = MessageDetail(messageLog, 2, false)
        rt!!.convertAndSend(DogsInitialApplication.QUEUE_NAME_ERROR, message)

        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.BAD_REQUEST.value()
        errorDetail.title = ex.propertyName
        errorDetail.detail = ex.message
        errorDetail.developerMessage = request.getDescription(true)

        return ResponseEntity(errorDetail, null, HttpStatus.NOT_FOUND)
    }

    override fun handleNoHandlerFoundException(ex: NoHandlerFoundException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any>
    {
        val messageLog: String = "$status on ${LocalDateTime.now()}"
        val message = MessageDetail(messageLog, 2, false)
        rt!!.convertAndSend(DogsInitialApplication.QUEUE_NAME_ERROR, message)

        val errorDetail = ErrorDetail()
        errorDetail.setTimestamp(Date().time)
        errorDetail.status = HttpStatus.NOT_FOUND.value()
        errorDetail.title = ex.requestURL
        errorDetail.detail = request.getDescription(true)
        errorDetail.developerMessage = "Rest Handler Not Found (check for valid URI)"

        return ResponseEntity(errorDetail, null, HttpStatus.NOT_FOUND)
    }
}

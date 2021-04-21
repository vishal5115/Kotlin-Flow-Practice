package com.example.kotlinflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    lateinit var fixedFlow: Flow<Int>
    lateinit var collectionFlow: Flow<Int>
    lateinit var lambdaFlow: Flow<Int>
    lateinit var channelFlow: Flow<Int>
    private val list = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpFixedFlow()
        setUpFlowForCollections()
        setUpFlowForLAmbdas()
        setUpForChannels()

        fixed.setOnClickListener {
            colectFixedFlow()
        }

        collection.setOnClickListener {
            collectCollectionFlow()
        }

        lambda.setOnClickListener {
            collectLambdaFlow()
        }

        channel.setOnClickListener {
            collectChannelFlow()
        }

        chain.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                createFlowChain()
            }
        }



        btnMapS.setOnClickListener {
            //Map sync
            runBlocking {
                (1..3).asFlow()
                        .map { num -> performSyncOperation(num) }
                        .collect { response -> println(response) }
            }
        }
        button6.setOnClickListener {
            //Map suspend
            runBlocking {
                (1..3).asFlow()
                        .map { num -> performSuspnedOperation(num) }
                        .collect { response -> println(response) }
            }
        }
        button3.setOnClickListener {
            //Filter Sync
            runBlocking {
                (1..3).asFlow()
                        .filter { num -> filterSync(num) }
                        .collect { response -> println(response) }
            }
        }
        button4.setOnClickListener {
            //Filter Suspend
            runBlocking {
                (1..3).asFlow()
                        .filter { num -> filterSyncSuspend(num) }
                        .collect { response -> println(response) }
            }
        }
        button.setOnClickListener {
            //Take
            runBlocking {
                (1..10).asFlow()
                        .take(4)
                        .collect { response -> println(response) }
            }
        }
        button2.setOnClickListener {
            //Take while
            val startTime = System.currentTimeMillis()
            runBlocking {
                (1..1000).asFlow()
                        .takeWhile {System.currentTimeMillis()-startTime<10}
                        .collect { response -> println(response) }
            }
        }

        button7.setOnClickListener{
            //ZIp Normal
            val nums  = (1..3).asFlow()
            val str = flowOf("one","two","three")
            runBlocking {
                nums.zip(str){a,b -> "$a $b"}
                        .collect{value -> println(value)}
            }
        }

        button8.setOnClickListener{
            //Zip one complete before another
            val nums  = (1..3).asFlow()
            val str = flowOf("one","two","three","four")
            runBlocking {
                nums.zip(str){a,b -> "$a $b"}
                        .collect{value -> println(value)}
            }
        }

        button9.setOnClickListener{
            //Zip when one emits after delay
            val nums  = (1..3).asFlow().onEach { delay(300) }
            val str = flowOf("one","two","three").onEach { delay(400) }
            runBlocking {
                nums.zip(str){a,b -> "$a $b"}
                        .collect{value -> println(value)}
            }
        }

        button10.setOnClickListener{
            //Combine when one emits after some delay
            val nums  = (1..3).asFlow().onEach { delay(300) }
            val str = flowOf("one","two","three").onEach { delay(400) }
            runBlocking {
                nums.combine(str){a,b -> "$a $b"}
                        .collect{value -> println(value)}
            }
        }
    }

    private suspend fun filterSyncSuspend(num: Int): Boolean {
        delay(100)
        return (num%2==0).not()
    }

    private fun filterSync(num: Int): Boolean {

        return (num%2==0).not()
    }


    private fun performSyncOperation(num: Int): String {

        return "response in sync $num"
    }

    private suspend fun performSuspnedOperation(num: Int): String {
        delay(300)
        return "response in sync $num"
    }


    // setup with fixed flow
    private fun setUpFixedFlow() {
        fixedFlow = flowOf(1, 2, 3, 4, 5, 6).onEach {
            delay(300)
        }
    }

    // setup with collection flow
    private fun setUpFlowForCollections() {
        collectionFlow = list.asFlow().onEach { delay(300) }
    }


    // setup with lambda flow
    private fun setUpFlowForLAmbdas() {
        lambdaFlow = flow {
            (1..5).onEach {
                delay(300)
                emit(it)
            }
        }
    }

    // setup with channel flow
    @ExperimentalCoroutinesApi
    private fun setUpForChannels() {
        channelFlow = channelFlow {
            (1..5).onEach {
                delay(300)
                send(it)
            }
        }
    }


    private fun colectFixedFlow() {
        CoroutineScope(Dispatchers.Main).launch {
            fixedFlow.collect { item ->
                Log.e("**Fixed Flow", "$item")
            }
        }
    }

    private fun collectCollectionFlow() {
        CoroutineScope(Dispatchers.Main).launch {
            collectionFlow.collect { item ->
                Log.e("**Collection Flow", "$item")
            }
        }
    }

    private fun collectLambdaFlow() {

        CoroutineScope(Dispatchers.Main).launch {
            lambdaFlow.collect { item ->
                Log.e("**Lambda Flow", "$item")

            }
        }
    }

    private fun collectChannelFlow() {
        CoroutineScope(Dispatchers.Main).launch {
            channelFlow.collect { item ->
                Log.e("**Channel Flow", "$item")
            }
        }
    }

    // create chain flow
    private suspend fun createFlowChain() {
        flow {
            (0..10).onEach {
                delay(300)
                emit(it)
            }
        }.flowOn(Dispatchers.IO).collect {
            Log.e("**Fixed Flow", "$it")
        }
    }
}
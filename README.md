# Java Library to plot the Time Complexity of methods!
### Download the latest .jar from the ["Releases"](https://github.com/TheBigSasha/RuntimeTester/releases)
You should use the .jar compiled for your operating system (RuntimeTester-windows.jar, RuntimeTester-MacOS.jar, or RuntimeTester-Unix.jar), or compile this project using shadowJAR for your OS if it is not one of those 3.

## 📚 Library Installation
Add the latest .jar from the [releases](https://github.com/TheBigSasha/RuntimeTester/releases) as a library to your Java IDE:

### IntelliJ IDEA
#### [Video Guides](https://www.youtube.com/watch?v=FBoE2F2152s&list=PLFvevpoGcNCs0p6QQOEASIuCRwDJAeioA&ab_channel=COMP250) 
##### Click on: ```File -> Project Structure -> Project Settings -> Libraries -> + -> Java -> (navigate to and select RuntimeTester.jar)```

### Eclipse
#### [Video Guides](https://www.youtube.com/watch?v=dofSJCqE9qE&list=PLFvevpoGcNCs5z8OeGYkws02bBrfeonVP&ab_channel=COMP250)
##### Right click on your project ``` -> Build Path -> Libraries -> Classpath -> Add External JARs... -> (navigate to and select RuntimeTester.jar)```!

## 💻 Usage
For a complete example, see the [sample project](https://github.com/TheBigSasha/RuntimeTester_DemoProject)

### 🚀 Launching the GUI
To launch the GUI with the default demos,
```java
import RuntimeTester.*;
public class MyClass{
  public static void main(String args[]){
      Visualizer.launch(MyClass.class);  //This line initializes and starts the GUI application. The class(es) you pass as parameters will be scanned for test methods.
  }
}
```
### 🧠 Adding your own method
To add your own test methods, use the @benchmark() annotation, as below:
```java
@benchmark(name = "hello world")                      //The @benchmark annotation has a required property "name", all others are optional
public static long testMethod(long input){            //All benchmark methods must be public, take long, return long
    //My-code-here
}
```
**Every custom test method must:**
  be _public_ so it can be called by the library
  be _static_ so that it can be called without instantiation
  _return long_ so that it can be plotted on the y axis
  _take long as the only parameter_ so it can be plotted on the x axis
  
 Here is a sample method which plots the curve of n^2
 ```java
    @benchmark(name = "sort", expectedEfficiency = "o(n^2)", category = "Math demos", theoretical = true)
    public static long nSquared(long size) {          //There is no restriction on method name
        return Math.round(Math.pow(size , 2));        //The x axis plots size and the y axis plots what is returned
    }
 ```

### 🏎️ Benchmarking real methods
Use the long parameter to indicate the number of items in your data structure

Here is a demo testing Java's built in sorting algorithm

 ```java
    @benchmark(name = "ArrayList.sort", expectedEfficiency = "O(n log(n))", category = "Java Builtin")
    public static long arraysSort(long size) {
        ArrayList<Date> dataset = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            dataset.add(nextDate());              //nextDate() is a method which randonly generates Java.Util.Date
                                                  //objects, for which you can find source code in the demonstration
                                                  //repository for this library (link below)
        }
        long startTime = System.nanoTime();       //This indicates when the timer on the method starts
        dataset.sort(Date::compareTo);
        long endTime = System.nanoTime();         //This indicates where the timer on the method ends
        return endTime - startTime;
    }
```

* if your method cannot run on more than an int amount of data, simply downcast the float input to int *

The general practice is:

  _Fill your dataset with N items
  store a startTime with System.nanoTime();
  Run your method
  store an endTime with System.nanoTime();
  return endTime - startTime_

## 📷 Screenshots and Expected Behaviour

![](https://sashaphotoca.files.wordpress.com/2020/10/2020-10-19-12_19_36-runtime-efficiency-wizard-comp250.png)
![](https://sashaphotoca.files.wordpress.com/2020/10/2020-10-19-12_18_06-runtime-efficiency-wizard-comp250.png)
![](https://sashaphotoca.files.wordpress.com/2020/10/2020-10-19-12_07_26-runtime-efficiency-wizard-comp250.png)

## 📁 Cloning as a Gradle project
### IntelliJ IDEA
Click on VCS -> get from version control -> paste the link for this repository -> run with Gradle

## 💖 Contributing, Contact, and Feedback
Dev contact: alexander.aleshchenko@mail.mcgill.ca
Website: sashaphoto.ca
Contributing: read contributing.md

# COMP250_Final_Debugger
Final Debugger for COMP250 at McGill University

# Install Instructions
## IMPORTANT: JavaFX Library Installation
This project uses JavaFX for the GUI.
JavaFX is the latest graphics library for Java, and for ***the features it adds will be worth the effort, I promise***.

#### Download JavaFX 14 SDK:
Direct Links:
[Windows](https://gluonhq.com/download/javafx-14-0-1-sdk-windows/)
[Mac](https://gluonhq.com/download/javafx-14-0-1-sdk-mac/)
[Linux](https://gluonhq.com/download/javafx-14-0-1-sdk-linux/)

Extract the downloaded library to a folder of your choice.

For my example (Windows), I extracted it to a folder <code>C:\Libraries\Java\javafx-sdk-14\ </code>
Do the same if you want to follow along exactly.

#### Clone this project, same as before.
##### IntelliJ Idea:
VCS -> Get from Version Control... -> Paste the URL of this repository

##### Eclipse:
File -> Import -> Git -> Projects from Git (With Smart Import) -> Clone URI -> paste URL of this repository into the URI box -> Click next a bunch, setting directory at your own discression, Master branch from origin. All else default -> Finish

##### Both:
Do the below using Finder or File Explorer, not your IDE:
Add your assignment .java files and any of your own classes to the COMP250_A4_W2020 package, in the location of Put Your java files here.txt.

##### NOTE: You must add the files from the professor's stress tester (.java files into the same place as your code) (.csv and .txt support files into the root directory)

##### NOTE: You must add your .java files into this project.It will not work if you drag my files into your project unless you really know what you are doing.

#### Configure your IDE for JavaFX:

##### IntelliJ Idea:
Open the cloned project in your IDE.

Click:
File -> Project Structure -> Libraries -> + -> Java -> Navigate to the "lib" folder inside of the JavaFX SDK you downloaded.

Attempt to run <code>MainWindow.java</code>. ***This will fail.*** It's ok.

Click:
Run -> Edit Configurations -> Select MainWindow -> under VM Options, paste: <code>--module-path ***YOUR PATH HERE*** --add-modules javafx.controls,javafx.fxml</code>

Where "***YOUR PATH HERE***" is the path of your JavaFX library download (for me it's C:\Libraries\Java\javafx-sdk-14\lib)

Attempt to run <code>MainWindow.java</code> again. It should work.

For more detail, read the [official instructions](https://www.jetbrains.com/help/idea/javafx.html)

##### Eclipse:
##### IMPORTANT: WHEN YOU CLONE, USE THE ECLIPSE BRANCH
##### Help wanted!
If you want to collaborate on an easy intsall procedure for Eclipse, please fork this repository or contact me!

###### Step 1: [Intstall IntelliJ IDEA](https://www.jetbrains.com/community/education/#students), it is free for students.
I presently do not offer Eclipse support for the runtime analysis part of this application. This will change as more brave users test JavaFX setup in eclipse.


###### If you do not:

Remove the COMP250_A4_W2020_Test_Visualizer_JFX folder from your directory.

You may still run unit tests by running HashTableUnitTester.java

## Screenshots

![iterator speed test](https://sashaphotoca.files.wordpress.com/2020/04/2020-04-26-19_19_16-runtime-efficiency-wizard-_3-sashaphoto.ca-tweet-visualizer.png)

![sort unit test](https://sashaphotoca.files.wordpress.com/2020/04/2020-04-26-20_33_30-runtime-efficiency-wizard-_3-sashaphoto.ca-tweet-visualizer.png)

## Recommended Folder Structure
![Folder structure](https://sashaphotoca.files.wordpress.com/2020/04/2020-04-26-21_12_19-comp250_final_debugger-e28093-controller.java-intellij-idea.png)

## Contributing, Help, and Contact
Feel free to fork and pull request this repository. If you have any questions, concerns, or feedback do not hesitate to contact me at sasha@sashaphoto.ca


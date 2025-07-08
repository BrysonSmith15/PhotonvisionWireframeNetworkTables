# Function
This program is to use photonvision (and photonlib)'s wireframe drawing feature in simulation. Since this is only available in java, this program is robot code to run in java and read from network tables the appropriate data for wireframe simulation.
## Usage
1) Add sending of Transform3d objects across networktables for each of your camera offsets. See [Here at lines 90-101](https://github.com/StolbergC/9445-Robot-2025/blob/5be899f756349c15b78c29886da4e1742ab74822/subsystems/vision.py)
2) Make sure that the names sent ("fl", "fr", "bl", "br" in above example) are the same as those on line 40 of Robot.java.
3) Run your robot code in simulation as you normally would. 
4) Run this robot code in simulation
5) Your camera wireframes should be available as specified in the photonvision documentation following the camera server protocol (localhost:1181 for camera 1 raw, localhost:1182 for camera 1 processed, localhost:1183 for camera 2 raw, etc)
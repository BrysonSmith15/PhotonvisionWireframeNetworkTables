// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.StructSubscriber;
import edu.wpi.first.networktables.NetworkTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.photonvision.simulation.*;
import org.photonvision.PhotonCamera;

/**
 * The methods in this class are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  private NetworkTableInstance nettable = NetworkTableInstance.getDefault();
  private ArrayList<String> cameraNames = new ArrayList<>(Arrays.asList("fl", "fr", "bl", "br"));
  private HashMap<String, Boolean> cameraSetup = new HashMap<>();
  private NetworkTable cameraTable;
  private VisionSystemSim visionSim;
  private StructSubscriber<Pose2d> poseTopic;
  private Pose2d prevPose = new Pose2d();

  public Robot() {
    nettable.stopServer();
    nettable.setServer("localhost");
    nettable.startClient4("Vision Sim");
    for (String name : cameraNames) {
      cameraSetup.put(name, false);
    }
    cameraTable = nettable.getTable(Constants.CAMERA_NT_PATH);
    visionSim = new VisionSystemSim("Vision Sim");
    visionSim.addAprilTags(AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField));
    poseTopic = nettable.getStructTopic(Constants.POSE_PATH, Pose2d.struct).subscribe(prevPose,
        PubSubOption.sendAll(true));
  }

  @Override
  public void simulationPeriodic() {
    for (String key : cameraNames) {
      if (!cameraSetup.get(key)) {
        System.out.println("Set up " + key);
        // get the transform3d out of the networktable
        NetworkTable subtable = cameraTable.getSubTable(key);
        double x = subtable.getEntry("x").getDouble(0);
        double y = subtable.getEntry("y").getDouble(0);
        double z = subtable.getEntry("z").getDouble(0);
        double roll = subtable.getEntry("roll").getDouble(0);
        double pitch = subtable.getEntry("pitch").getDouble(0);
        double yaw = subtable.getEntry("yaw").getDouble(0);

        PhotonCamera cam = new PhotonCamera(key);
        PhotonCameraSim simCam = new PhotonCameraSim(cam, Constants.CAMERA_PROPS);

        visionSim.addCamera(simCam, new Transform3d(new Translation3d(x, y, z), new Rotation3d(roll, pitch, yaw)));
        simCam.enableDrawWireframe(true);
        cameraSetup.put(key, true);
      }
    }
    Pose2d currPose = poseTopic.get(this.prevPose);
    visionSim.update(currPose);
    this.prevPose = currPose;
  }
}

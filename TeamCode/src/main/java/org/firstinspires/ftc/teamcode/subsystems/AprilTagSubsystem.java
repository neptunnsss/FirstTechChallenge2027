package org.firstinspires.ftc.teamcode.subsystems;


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class AprilTagSubsystem {
    public Limelight3A limelight3A;
    public LLResult LLResult;
    private DriveSubsystem drive;
    public void init(HardwareMap hardwareMap){

        limelight3A = hardwareMap.get(Limelight3A .class, "limelight3A");
        limelight3A.pipelineSwitch(0);
        drive = new DriveSubsystem(hardwareMap);
        limelight3A.start();

    }
    public void update(){
        YawPitchRollAngles orientation = drive.imu.getRobotYawPitchRollAngles();
        limelight3A.updateRobotOrientation(orientation.getYaw());
        LLResult = limelight3A.getLatestResult();
        if (LLResult != null && LLResult.isValid()) {
            Pose3D pose = LLResult.getBotpose_MT2();
    }
}}

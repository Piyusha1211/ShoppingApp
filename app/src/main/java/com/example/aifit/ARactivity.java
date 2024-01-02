package com.example.aifit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.HashMap;
import java.util.Objects;

public class ARactivity extends AppCompatActivity {
    private int clickNo = 0;
    private ArFragment arCam;
    private String name;
    HashMap<String, ARProductData> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);

        hashMap.put("T-Shirt", new ARProductData(R.raw.shirt1, 0.59f, 0.6f));
        hashMap.put("Pant", new ARProductData(R.raw.pant2, 0.62f, 0.65f));
        hashMap.put("Coat", new ARProductData(R.raw.coat1, 0.62f, 0.66f));
        hashMap.put("Shorts", new ARProductData(R.raw.shorts1, 0.65f, 0.67f));
        hashMap.put("Caps", new ARProductData(R.raw.cap1, 0.66f, 0.69f));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
        }

        if (checkSystemSupport(this)) {
            arCam = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

            assert arCam != null;
            arCam.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                clickNo++;
                if (clickNo == 1) {
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable.builder()
                            .setSource(this, hashMap.get(name).getId())
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> addModel(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Something is not right" + throwable.getMessage()).show();
                                return null;
                            });
                }
            });
        }
    }

    private void addModel(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arCam.getArSceneView().getScene());

        TransformableNode model = new TransformableNode(arCam.getTransformationSystem());
        model.setParent(anchorNode);
        model.getScaleController().setMaxScale(hashMap.get(name).getMax());
        model.getScaleController().setMinScale(hashMap.get(name).getMin());
        model.setRenderable(modelRenderable);
        model.select();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (arCam != null && arCam.getArSceneView() != null) {
            arCam.getArSceneView().destroy();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ARactivity.this, MainActivity.class);
        intent.putExtra("name", "Piyusha");
        startActivity(intent);
    }

    public static boolean checkSystemSupport(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();
            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }
}

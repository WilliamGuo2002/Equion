package com.example.equion;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreController {
    // Firebase Firestore 控制器类，用于管理用户数据和持仓信息
    private final FirebaseFirestore db;
    private final String uid;

    public FirestoreController(String uid) {
        this.db = FirebaseFirestore.getInstance();
        this.uid = uid;
    }

    // 初始化用户数据（仅在首次登录时）
    public void initUserIfFirstTime(String email) {
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("name", "New User");
                userData.put("createdAt", FieldValue.serverTimestamp());
                userData.put("holdings", new ArrayList<String>());
                userData.put("favorites", new ArrayList<String>());
                docRef.set(userData)
                        .addOnSuccessListener(unused -> Log.d("FIRESTORE", "初始化成功"))
                        .addOnFailureListener(e -> Log.w("FIRESTORE", "初始化失败", e));
            }
        });
    }

    // 更新持仓信息（替换整个 holdings 字段）
    public void updateHoldings(List<String> newHoldings) {
        db.collection("users").document(uid)
                .update("holdings", newHoldings)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "持仓更新成功"))
                .addOnFailureListener(e -> Log.w("FIRESTORE", "更新持仓失败", e));
    }

    // 添加自选股
    public void addFavorite(String ticker) {
        db.collection("users").document(uid)
                .update("favorites", FieldValue.arrayUnion(ticker))
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "已添加自选股: " + ticker))
                .addOnFailureListener(e -> Log.w("FIRESTORE", "添加自选股失败", e));
    }

    // 删除自选股
    public void removeFavorite(String ticker) {
        db.collection("users").document(uid)
                .update("favorites", FieldValue.arrayRemove(ticker))
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "已删除自选股: " + ticker))
                .addOnFailureListener(e -> Log.w("FIRESTORE", "删除自选股失败", e));
    }
}
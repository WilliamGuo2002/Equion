package com.example.equion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firebase 后端统一控制器
 * 负责用户认证、Watchlist 股票管理、Orion 聊天记录存取。
 */
public class FirebaseController {

    private static FirebaseController instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private FirebaseController() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseController getInstance() {
        if (instance == null) {
            instance = new FirebaseController();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /** 获取当前用户文档引用 */
    public DocumentReference getUserDoc() {
        FirebaseUser user = getCurrentUser();
        if (user == null) return null;
        return db.collection("users").document(user.getUid());
    }

    // ---------- Watchlist ----------
    /** 获取 Watchlist 子集合引用 */
    public CollectionReference getWatchlistRef() {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return null;
        return userDoc.collection("watchlist");
    }

    /** 添加股票代码到 Watchlist */
    public void addStockToWatchlist(String symbol) {
        CollectionReference watchlist = getWatchlistRef();
        if (watchlist == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put("addedAt", com.google.firebase.Timestamp.now());
        watchlist.document(symbol).set(data);
    }

    /** 从 Watchlist 移除股票 */
    public void removeStockFromWatchlist(String symbol) {
        CollectionReference watchlist = getWatchlistRef();
        if (watchlist == null) return;
        watchlist.document(symbol).delete();
    }

    /** 获取 Watchlist 查询（按添加时间排序） */
    public Query getWatchlistQuery() {
        CollectionReference watchlist = getWatchlistRef();
        if (watchlist == null) return null;
        return watchlist.orderBy("addedAt");
    }

    // ---------- Orion 聊天记录 ----------
    /** 获取 Chat 历史子集合引用 */
    public CollectionReference getChatHistoryRef() {
        DocumentReference userDoc = getUserDoc();
        if (userDoc == null) return null;
        return userDoc.collection("chats");
    }

    /** 保存某个聊天（messages 是 [ {role: "user"/"ai", content: "文本"} ] 结构） */
    public void saveChatMessage(String chatId, List<Map<String, String>> messages) {
        CollectionReference chats = getChatHistoryRef();
        if (chats == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", com.google.firebase.Timestamp.now());
        data.put("messages", messages);
        chats.document(chatId).set(data);
    }

    /** 获取按时间倒序的聊天记录 */
    public Query getChatHistoryQuery() {
        CollectionReference chats = getChatHistoryRef();
        if (chats == null) return null;
        return chats.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    // ---------- 认证相关 ----------
    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
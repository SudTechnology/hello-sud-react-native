package com.rtnsudgip.sudgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import tech.sud.gip.SudGIPWrapper.model.GameConfigModel;
import tech.sud.gip.SudGIPWrapper.model.GameViewInfoModel;

public class SudGameManager  {

    public static SudGameManager shared = new SudGameManager();

    private  QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel();

    private GameView gameView;

    private Activity currentActivity = null;
    private SudMGPPlugin plugin = null;

    public void setSudMgpPlugin(SudMGPPlugin plugin) {
        plugin = plugin;
        gameViewModel.setSudMgpPlugin(plugin);
    }

    public void setCurrentActivity(AppCompatActivity currentActivity) {

        if (gameViewModel != null) {
            gameViewModel.destroyMG();
        } 
        gameViewModel = new QuickStartGameViewModel();
        if (plugin != null) {
            gameViewModel.setSudMgpPlugin(plugin);
        }
        

        this.currentActivity = currentActivity;
        // 设置游戏安全操作区域
        // Set the secure operation area for the game.
        GameViewInfoModel.GameViewRectModel gameViewRectModel = new GameViewInfoModel.GameViewRectModel();
        gameViewRectModel.left = 0;
        gameViewRectModel.top = 0;
        gameViewRectModel.right = 0;
        gameViewRectModel.bottom = 0;
        gameViewModel.gameViewRectModel = gameViewRectModel;



        gameViewModel.gameViewLiveData.observe(currentActivity, new Observer<View>() {
            @Override
            public void onChanged(View view) {

                if (view == null) { // 在关闭游戏时，把游戏View给移除 English: When closing the game, remove the game view.
                    gameView.removeAllViews();
                } else { // 把游戏View添加到容器内 English: Add the game view to the container.
                    gameView.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
    }

    public FrameLayout getGameView(Context ctx) {
        if (gameView == null) {
            gameView = new GameView(ctx);
        }
        return gameView;
    }

    public void handleOnResume() {
        gameViewModel.onResume();
    }

    public void handleOnPause() {
        gameViewModel.onPause();
    }

    public void handleOnDestroy() {
        gameViewModel.destroyMG();
    }

    public Boolean loadGame(String param) {

        try {

            if (currentActivity == null){
                Toast.makeText(null, "loadGame currentActivity is empty", Toast.LENGTH_LONG).show();
                return false;
            }

            JSONObject jsonObject = new JSONObject(param);
                        // Extract fields from JSONObject
                        String appId = jsonObject.getString("appId");
                        String appKey = jsonObject.getString("appKey");
                        boolean isTestEnv = jsonObject.getBoolean("isTestEnv");
                        long gameId =  Long.parseLong(jsonObject.getString("gameId")) ;
                        String roomId = jsonObject.getString("roomId");
                        String userId = jsonObject.getString("userId");
                        String language = jsonObject.getString("language");
                        String authorizationSecret = jsonObject.getString("authorizationSecret");
                        String code = jsonObject.getString("code");

            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                        QuickStartGameViewModel.GAME_IS_TEST_ENV = isTestEnv;
                        QuickStartGameViewModel.SudMGP_APP_ID = appId;
                        QuickStartGameViewModel.SudMGP_APP_KEY = appKey;
                        QuickStartGameViewModel.userId = userId;
                        gameViewModel.languageCode = language;
                        gameViewModel.updateCode(code);

                        gameViewModel.switchGame(currentActivity,roomId, gameId);

                }
            });
            return true;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void configGameRect(String param) {
        if (param.isEmpty()) {
            return;
        }
        Gson gson = new Gson();
        GameViewInfoModel.GameViewRectModel gameViewRectModel = gson.fromJson(param, GameViewInfoModel.GameViewRectModel.class);
        if (gameViewRectModel!=null) {
            gameViewModel.gameViewRectModel = gameViewRectModel;
        }
    }

    public void configGameCfg(String param) {
        if (param.isEmpty()) {
            return;
        }
        Gson gson = new Gson();
        GameConfigModel configModel = gson.fromJson(param, GameConfigModel.class);
        if (configModel!=null) {
            gameViewModel.gameConfigModel = configModel;
        }
    }

    public void destroyGame() {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameViewModel.destroyMG();
                gameView.removeAllViews();
            }
        });

    }

    public void updateCode(String param) {
        try{
            JSONObject jsonObject = new JSONObject(param);
            String code = jsonObject.getString("code");
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameViewModel.updateCode(code);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void notifyStateChange(String param) {
        try{
            JSONObject jsonObject = new JSONObject(param);
            String state = jsonObject.getString("state");
            String dataJson = jsonObject.getString("dataJson");
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameViewModel.notifyStateChange(state, dataJson);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

//
//  SudMGPPluginManager.m
//  Demo
//
//  Created by kaniel on 4/1/25.
//

#import "SudMGPPlugin.h"
#import "SudGameManager.h"
#import <MJExtension/MJExtension.h>
#import "QuickStartSudGameEventHandler.h"

#define kKeyOnGameStateChanged @"onGameStateChanged"
#define kKeyOnPlayerStateChanged @"onPlayerStateChanged"
#define kKeyOnGetCode @"onGetCode"

@interface SudGameHolder : NSObject

@property(nonatomic, strong)SudGameManager *gameManager;
@property(nonatomic, strong)QuickStartSudGameEventHandler *gameHandler;
@property(nonatomic, strong)UIView *gameView;
+(instancetype)shared;
@end

@implementation SudGameHolder

+(instancetype)shared {
  static SudGameHolder *g_instance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    g_instance = [[SudGameHolder alloc]init];
    [g_instance prepare];
  });
  return g_instance;
}

- (SudGameManager *)gameManager {
  if (!_gameManager) {
    _gameManager = [[SudGameManager alloc]init];
    
    
  }
  return _gameManager;
}

- (QuickStartSudGameEventHandler *)gameHandler {
  if (!_gameHandler) {
    _gameHandler = QuickStartSudGameEventHandler.new;
  }
  return _gameHandler;
}

- (UIView *)gameView {
  if(!_gameView) {
    _gameView = [[UIView alloc]init];
  }
  return _gameView;
}

- (void)prepare {
  [self.gameManager registerGameEventHandler:self.gameHandler];
}

@end


@implementation SudMGPPlugin

-(NSDictionary *)convertJsonDic:(NSString *)jsonString {
  
  NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
  
  // Convert NSData to NSDictionary
  NSError *error;
  NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:&error];
  return dictionary;
}

-(instancetype)init {
  if (self = [super init]) {
    SudGameHolder.shared.gameHandler.plugin = self;
  }
  return self;
}

RCT_EXPORT_MODULE();
- (NSArray<NSString *> *)supportedEvents {
  
  return @[kKeyOnGameStateChanged,kKeyOnPlayerStateChanged,kKeyOnGetCode]; // Define the event names you want to send
}

RCT_EXPORT_METHOD(configGameRect:(NSString *)param
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {

  // Load your game logic here
  ViewGameRect *gameRect = [ViewGameRect mj_objectWithKeyValues:param];
  SudGameHolder.shared.gameHandler.gameRect = gameRect;
  resolve(@"Game configGameRect");
  
}

RCT_EXPORT_METHOD(configGameCfg:(NSString *)param
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {

  // Load your game logic here
  GameCfgModel *gameCfg = [GameCfgModel mj_objectWithKeyValues:param];
  SudGameHolder.shared.gameHandler.gameCfg = gameCfg;
  resolve(@"Game configGameCfg");
  
}

RCT_EXPORT_METHOD(loadGame:(NSString *)param
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {

  // Load your game logic here
  SudGameLoadConfigModel *configModel = [SudGameLoadConfigModel mj_objectWithKeyValues:param];
  configModel.gameView = SudGameHolder.shared.gameView;
  [SudGameHolder.shared.gameManager loadGame:configModel success:nil fail:nil];
  resolve(@"Game Loaded");
  
}

RCT_EXPORT_METHOD(destroyGame:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  // Close your game logic here
  [SudGameHolder.shared.gameManager destroyGame];
  resolve(@"Game Destroied");
  
}

  
  RCT_EXPORT_METHOD(notifyStateChange:(NSString *)param
                    resolver:(RCTPromiseResolveBlock)resolve
                    rejecter:(RCTPromiseRejectBlock)reject) {
    NSDictionary *paramDic = [self convertJsonDic:param];
    [SudGameHolder.shared.gameHandler notifyStateChange:paramDic[@"state"] dataJson:paramDic[@"dataJson"]];
    resolve(@"");
  }

RCT_EXPORT_METHOD(updateCode:(NSString *)param
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  NSDictionary *paramDic = [self convertJsonDic:param];
  NSString *code = paramDic[@"code"];
  [SudGameHolder.shared.gameHandler updateCode:code];
  resolve(@"");
}

- (void)onGameStateChanged:(NSString *)state dataJson:(NSString *)dataJson {
  [self sendEventToReactNative:kKeyOnGameStateChanged body:@{@"state":state, @"dataJson":dataJson ? dataJson : @"{}"}];
}

- (void)onPlayerStateChanged:(NSString *)state dataJson:(NSString *)dataJson {
  [self sendEventToReactNative:kKeyOnPlayerStateChanged body:@{@"state":state, @"dataJson":dataJson ? dataJson : @"{}"}];
  
}

- (void)handleOnGetCode:(NSString *)userId appId:(NSString *)appId {
  [self sendEventToReactNative:kKeyOnGetCode body:@{@"userId":userId, @"appId":appId ? appId : @""}];
}

- (void)sendEventToReactNative:(NSString *)event body:(id)body {
  [self sendEventWithName:event body:body];
}

- (void)dealloc {
  NSLog(@"SudMGPPlugin dealloc");
}
@end

@implementation SudMGPPluginViewManager
RCT_EXPORT_MODULE()
- (UIView *)view {
  UIView *view = SudGameHolder.shared.gameView;
  view.backgroundColor = [UIColor blueColor]; // Customize your view as needed
  return view;
}

@end

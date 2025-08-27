//
//  SudMGPPluginManager.h
//  Demo
//
//  Created by kaniel on 4/1/25.
//
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>
NS_ASSUME_NONNULL_BEGIN

@interface SudMGPPlugin : RCTEventEmitter <RCTBridgeModule>
- (void)onGameStateChanged:(NSString *)state dataJson:(NSString *)dataJson;
- (void)onPlayerStateChanged:(NSString *)state dataJson:(NSString *)dataJson;
- (void)handleOnGetCode:(NSString *)userId appId:(NSString *)appId;
@end

@interface SudMGPPluginViewManager : RCTViewManager

@end

NS_ASSUME_NONNULL_END

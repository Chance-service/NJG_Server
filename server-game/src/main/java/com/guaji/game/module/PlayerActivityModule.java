package com.guaji.game.module;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.AccConsumeCfg;
import com.guaji.game.config.AccConsumeItemCfg;
import com.guaji.game.config.Activity139TimesCfg;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityDailyQuest196Cfg;
import com.guaji.game.config.ActivityDailyQuestCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ActivityURTimes127;
import com.guaji.game.config.ConsumeWeekCardCfg;
import com.guaji.game.config.FairyBlessCfg;
import com.guaji.game.config.GloryHoleDailyCfg;
import com.guaji.game.config.HalloweenConstCfg;
import com.guaji.game.config.HaremConstCfg;
import com.guaji.game.config.NewTreasureRaiderTimesCfg;
import com.guaji.game.config.NewTreasureRaiderTimesCfg2;
import com.guaji.game.config.NewTreasureRaiderTimesCfg3;
import com.guaji.game.config.NewTreasureRaiderTimesCfg4;
import com.guaji.game.config.NewURTimesCfg;
import com.guaji.game.config.ObonStageCfg;
import com.guaji.game.config.ObonTimesCfg;
import com.guaji.game.config.PrinceDevilsCostCfg;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.ReleaseURTimesCfg121;
import com.guaji.game.config.ReleaseURTimesCfg123;
import com.guaji.game.config.ReleaseURTimesCfg2;
import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.config.SevenDayQuestPointCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TurntableConstCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.config.WelfareRewardCfg;
import com.guaji.game.config.accLoginSignedPointCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.entity.ExpeditionArmoryEntity;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.ExpeditionArmoryManager;
import com.guaji.game.module.activity.RemoveRedPointHandler;
import com.guaji.game.module.activity.ActiveCompliance.ActiveComplianceHandler;
import com.guaji.game.module.activity.ActiveCompliance.ActiveComplianceInfoHandler;
import com.guaji.game.module.activity.ActiveCompliance.ActiveStatus;
import com.guaji.game.module.activity.activity121.ReleaseURHandler121;
import com.guaji.game.module.activity.activity121.ReleaseURInfoHandler121;
import com.guaji.game.module.activity.activity121.ReleaseURLotteryHandler121;
import com.guaji.game.module.activity.activity121.ReleaseURResetHandler121;
import com.guaji.game.module.activity.activity121.ReleaseURStatus121;
import com.guaji.game.module.activity.activity123.Activity123Handler;
import com.guaji.game.module.activity.activity123.Activity123InfoHandler;
import com.guaji.game.module.activity.activity123.Activity123LotteryHandler;
import com.guaji.game.module.activity.activity123.Activity123Status;
import com.guaji.game.module.activity.activity124.Activity124InfoHandler;
import com.guaji.game.module.activity.activity124.Activity124LotteryHandler;
import com.guaji.game.module.activity.activity124.Activity124Status;
import com.guaji.game.module.activity.activity125.Activity125InfoHandler;
import com.guaji.game.module.activity.activity125.Activity125StartHandler;
import com.guaji.game.module.activity.activity125.Activity125Status;
import com.guaji.game.module.activity.activity127.Activity127Handler;
import com.guaji.game.module.activity.activity127.Activity127InfoHandler;
import com.guaji.game.module.activity.activity127.Activity127Status;
import com.guaji.game.module.activity.activity128.Activity128BoxHandler;
import com.guaji.game.module.activity.activity128.Activity128InfoHandler;
import com.guaji.game.module.activity.activity128.Activity128LotteryHandler;
import com.guaji.game.module.activity.activity128.Activity128RankHandler;
import com.guaji.game.module.activity.activity128.Activity128Status;
import com.guaji.game.module.activity.activity132.Activity132BuyHandler;
import com.guaji.game.module.activity.activity132.Activity132InfoHandler;
import com.guaji.game.module.activity.activity132.Activity132Status;
import com.guaji.game.module.activity.activity134.Activity134GetHandler;
import com.guaji.game.module.activity.activity134.Activity134InfoHandler;
import com.guaji.game.module.activity.activity134.Activity134LotteryHandler;
import com.guaji.game.module.activity.activity134.Activity134Manager;
import com.guaji.game.module.activity.activity134.Activity134Status;
import com.guaji.game.module.activity.activity134.Activity134StatusItem;
import com.guaji.game.module.activity.activity137.Activity137InfoHandler;
import com.guaji.game.module.activity.activity137.Activity137LotteryHandler;
import com.guaji.game.module.activity.activity137.Activity137Status;
import com.guaji.game.module.activity.activity138.NewTreasureRaiderHandler139;
import com.guaji.game.module.activity.activity138.NewTreasureRaiderInfoHandler139;
import com.guaji.game.module.activity.activity138.NewTreasureRaiderStatus139;
import com.guaji.game.module.activity.activity140.Activity140LotteryHandler;
import com.guaji.game.module.activity.activity140.Activity140Status;
import com.guaji.game.module.activity.activity141.Activity141RichManHandler;
import com.guaji.game.module.activity.activity143.Activity143PirateHandler;
import com.guaji.game.module.activity.activity144.Activity144LittleTestHandler;
import com.guaji.game.module.activity.activity146.Activity146Handler;
import com.guaji.game.module.activity.activity146.Activity146InfoHandler;
import com.guaji.game.module.activity.activity147.Activity147Handler;
import com.guaji.game.module.activity.activity147.Activity147InfoHandler;
import com.guaji.game.module.activity.activity148.Activity148Handler;
import com.guaji.game.module.activity.activity151.Activity151BuyHandler;
import com.guaji.game.module.activity.activity151.Activity151InfoHandler;
import com.guaji.game.module.activity.activity151.Activity151Status;
import com.guaji.game.module.activity.activity152.Activity152Handler;
import com.guaji.game.module.activity.activity153.Activity153Handler;
import com.guaji.game.module.activity.activity154.Activity154Handler;
import com.guaji.game.module.activity.activity157.Activity157Handler;
import com.guaji.game.module.activity.activity158.Activity158Handler;
import com.guaji.game.module.activity.activity159.Activity159Handler;
import com.guaji.game.module.activity.activity160.Activity160Handler;
import com.guaji.game.module.activity.activity161.Activity161Handler;
import com.guaji.game.module.activity.activity162.Activity162Handler;
import com.guaji.game.module.activity.activity163.Activity163Handler;
import com.guaji.game.module.activity.activity164.Activity164Handler;
import com.guaji.game.module.activity.activity165.Activity165Handler;
import com.guaji.game.module.activity.activity166.Activity166Handler;
import com.guaji.game.module.activity.activity166.Activity166InfoHandler;
import com.guaji.game.module.activity.activity167.Activity167Handler;
import com.guaji.game.module.activity.activity168.Activity168Handler;
import com.guaji.game.module.activity.activity169.Activity169BuyHandler;
import com.guaji.game.module.activity.activity169.Activity169InfoHandler;
import com.guaji.game.module.activity.activity170.Activity170BuyHandler;
import com.guaji.game.module.activity.activity170.Activity170InfoHandler;
import com.guaji.game.module.activity.activity172.Activity172Handler;
import com.guaji.game.module.activity.activity172.Activity172InfoHandler;
import com.guaji.game.module.activity.activity173.Activity173Handler;
import com.guaji.game.module.activity.activity173.Activity173InfoHandler;
import com.guaji.game.module.activity.activity175.Activity175Handler;
import com.guaji.game.module.activity.activity175.Activity175Status;
import com.guaji.game.module.activity.activity176.Activity176ExchangeHandler;
import com.guaji.game.module.activity.activity177.Activity177Handler;
import com.guaji.game.module.activity.activity178.Activity178Handler;
import com.guaji.game.module.activity.activity178.Activity178InfoHandler;
import com.guaji.game.module.activity.activity179.Activity179Handler;
import com.guaji.game.module.activity.activity180.Activity180Handler;
import com.guaji.game.module.activity.activity181.Activity181BuyHandler;
import com.guaji.game.module.activity.activity181.Activity181InfoHandler;
import com.guaji.game.module.activity.activity182.Activity182BuyHandler;
import com.guaji.game.module.activity.activity182.Activity182InfoHandler;
import com.guaji.game.module.activity.activity183.Activity183BuyHandler;
import com.guaji.game.module.activity.activity183.Activity183InfoHandler;
import com.guaji.game.module.activity.activity184.Activity184BuyHandler;
import com.guaji.game.module.activity.activity184.Activity184InfoHandler;
import com.guaji.game.module.activity.activity185.Activity185BuyHandler;
import com.guaji.game.module.activity.activity185.Activity185InfoHandler;
import com.guaji.game.module.activity.activity186.Activity186BuyHandler;
import com.guaji.game.module.activity.activity186.Activity186InfoHandler;
import com.guaji.game.module.activity.activity187.Activity187Handler;
import com.guaji.game.module.activity.activity190.Activity190Handler;
import com.guaji.game.module.activity.activity191.Activity191Handler;
import com.guaji.game.module.activity.activity191.Activity191Status;
import com.guaji.game.module.activity.activity192.Activity192Handler;
import com.guaji.game.module.activity.activity193.Activity193Handler;
import com.guaji.game.module.activity.activity194.Activity194Handler;
import com.guaji.game.module.activity.activity196.Activity196Handler;
import com.guaji.game.module.activity.activity196.Activity196Status;
import com.guaji.game.module.activity.activity197.Activity197Handler;
import com.guaji.game.module.activity.activity197.Activity197InfoHandler;
import com.guaji.game.module.activity.chatSkin.ChatSkinBuyHandler;
import com.guaji.game.module.activity.chatSkin.ChatSkinChangeHandler;
import com.guaji.game.module.activity.chatSkin.ChatSkinClearRedPointHandler;
import com.guaji.game.module.activity.chatSkin.ChatSkinInfoHandler;
import com.guaji.game.module.activity.chatSkin.ChatSkinOwnedInfoHandler;
import com.guaji.game.module.activity.commendationTribe.CommendationTribeHandler;
import com.guaji.game.module.activity.consumMonthCard.ConMonthCardAwardGetHandler;
import com.guaji.game.module.activity.consumMonthCard.ConMonthCardInfoHandler;
import com.guaji.game.module.activity.consumMonthCard.ConMonthCardStatus;
import com.guaji.game.module.activity.consumWeekCard.ConWeekCardInfoHandler;
import com.guaji.game.module.activity.consumWeekCard.ConWeekCardRewardHandler;
import com.guaji.game.module.activity.consumWeekCard.ConWeekCardStatus;
import com.guaji.game.module.activity.consume.AccConsumeAwardHandler;
import com.guaji.game.module.activity.consume.AccConsumeInfoHandler;
import com.guaji.game.module.activity.consume.AccConsumeStatus;
import com.guaji.game.module.activity.consumeitem.AccConItemStatus;
import com.guaji.game.module.activity.consumeitem.AccConsumeItemAwardHandler;
import com.guaji.game.module.activity.consumeitem.AccConsumeItemInfoHandler;
import com.guaji.game.module.activity.consumeitem.ConsumeItem;
import com.guaji.game.module.activity.discountGift.DiscountGiftData;
import com.guaji.game.module.activity.discountGift.DiscountGiftGetRewardHandler;
import com.guaji.game.module.activity.discountGift.DiscountGiftInfoHandler;
import com.guaji.game.module.activity.exchange.DoExchangeHandler;
import com.guaji.game.module.activity.exchange.ExchangeInfoHandler;
import com.guaji.game.module.activity.exchangeShop.DoExchangeShopHandler;
import com.guaji.game.module.activity.exchangeShop.ExchangeShopInfoHandler;
import com.guaji.game.module.activity.exchangeShop.activity142DoExchangeShopHandler;
import com.guaji.game.module.activity.exchangeShop.activity142ExchangeShopInfoHandler;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryAllServerHandler;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryInfoHandler;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryRankingHandler;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryStatus;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryUseItemHandler;
import com.guaji.game.module.activity.fairyBless.FairyBlessHandler;
import com.guaji.game.module.activity.fairyBless.FairyBlessInfoHandler;
import com.guaji.game.module.activity.fairyBless.FairyBlessStatus;
import com.guaji.game.module.activity.firstgiftpack.FirstGiftPackAwardHandler;
import com.guaji.game.module.activity.firstgiftpack.FirstGiftPackInfoHandler;
import com.guaji.game.module.activity.firstgiftpack.FirstGiftPackStatus;
import com.guaji.game.module.activity.foreverCard.ForeverCardAvtivateHandler;
import com.guaji.game.module.activity.foreverCard.ForeverCardDailyAwardHandler;
import com.guaji.game.module.activity.foreverCard.ForeverCardInfoHandler;
import com.guaji.game.module.activity.forging.ForgingHandler;
import com.guaji.game.module.activity.forging.ForgingInfoHandler;
import com.guaji.game.module.activity.forging.ForgingStatus;
import com.guaji.game.module.activity.fortune.FortuneHandler;
import com.guaji.game.module.activity.fragmentExchange.FragmentExchangeHandler;
import com.guaji.game.module.activity.fragmentExchange.FragmentExchangeInfoHandler;
import com.guaji.game.module.activity.gem.GemCompoundHandler;
import com.guaji.game.module.activity.gem.GemCompoundInfoHandler;
import com.guaji.game.module.activity.goldfish.GoldfishFishingHandler;
import com.guaji.game.module.activity.goldfish.GoldfishInfoHandler;
import com.guaji.game.module.activity.goldfish.GoldfishRankHandler;
import com.guaji.game.module.activity.goldfish.GoldfishStatus;
import com.guaji.game.module.activity.goldfish.GoldfishViewHandler;
import com.guaji.game.module.activity.grabRedEnvelope.GiveRedEnvelopeHandler;
import com.guaji.game.module.activity.grabRedEnvelope.GrabFreeRedEnvelopeHandler;
import com.guaji.game.module.activity.grabRedEnvelope.GrabRedEnvelopeHandler;
import com.guaji.game.module.activity.grabRedEnvelope.RedEnvelopeInfoHandler;
import com.guaji.game.module.activity.growthFund.GrowthFundAwardGetHandler;
import com.guaji.game.module.activity.growthFund.GrowthFundBuyHandler;
import com.guaji.game.module.activity.growthFund.GrowthFundInfoHandler;
import com.guaji.game.module.activity.growthFund.GrowthFundStatus;
import com.guaji.game.module.activity.halloween.HalloweenHandler;
import com.guaji.game.module.activity.halloween.HalloweenManager;
import com.guaji.game.module.activity.halloween.HalloweenStatus;
import com.guaji.game.module.activity.harem.HaremExchangeRequestHandler;
import com.guaji.game.module.activity.harem.HaremHandler;
import com.guaji.game.module.activity.harem.HaremInfoHandler;
import com.guaji.game.module.activity.harem.HaremManager;
import com.guaji.game.module.activity.harem.HaremScorePanelInfoHandler;
import com.guaji.game.module.activity.holiday.HolidayTreasureHandler;
import com.guaji.game.module.activity.invite.ExchangeInviteCodeHandler;
import com.guaji.game.module.activity.invite.FriendInviteAwardHandler;
import com.guaji.game.module.activity.invite.FriendInviteHandler;
import com.guaji.game.module.activity.lights.FindTreasureInfoHandler;
import com.guaji.game.module.activity.lights.FindTreasureLightHandler;
import com.guaji.game.module.activity.log.ActivityClickRecordHandler;
import com.guaji.game.module.activity.login.AccLoginAwardsHandler;
import com.guaji.game.module.activity.login.AccLoginInfoHandler;
import com.guaji.game.module.activity.login.AccLoginStatus;
import com.guaji.game.module.activity.loginsigned.AccLoginSignedAwardsHandler;
import com.guaji.game.module.activity.loginsigned.AccLoginSignedInfoHandler;
import com.guaji.game.module.activity.loginsigned.AccLoginSignedOpenChestHandler;
import com.guaji.game.module.activity.loginsigned.AccLoginSignedStatus;
import com.guaji.game.module.activity.luckyMercenary.LuckyMercenaryInfoHandler;
import com.guaji.game.module.activity.luckyTreasure.LuckyTreasureInfoHandler;
import com.guaji.game.module.activity.luckyTreasure.LuckyTreasureRewardHandler;
import com.guaji.game.module.activity.maidenEncounter.MaidenEncounterExchangeHandler;
import com.guaji.game.module.activity.maidenEncounter.MaidenEncounterExchangeInfoHandler;
import com.guaji.game.module.activity.maidenEncounter.MaidenEncounterHandler;
import com.guaji.game.module.activity.monthcard.MonthCardAwardGetHandler;
import com.guaji.game.module.activity.monthcard.MonthCardInfoHandler;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.activity.newTreasureRaider.NewTreasureRaiderHandler;
import com.guaji.game.module.activity.newTreasureRaider.NewTreasureRaiderInfoHandler;
import com.guaji.game.module.activity.newTreasureRaider.NewTreasureRaiderStatus;
import com.guaji.game.module.activity.newTreasureRaider2.NewTreasureRaiderHandler2;
import com.guaji.game.module.activity.newTreasureRaider2.NewTreasureRaiderInfoHandler2;
import com.guaji.game.module.activity.newTreasureRaider2.NewTreasureRaiderStatus2;
import com.guaji.game.module.activity.newTreasureRaider3.NewTreasureRaiderHandler3;
import com.guaji.game.module.activity.newTreasureRaider3.NewTreasureRaiderInfoHandler3;
import com.guaji.game.module.activity.newTreasureRaider3.NewTreasureRaiderStatus3;
import com.guaji.game.module.activity.newTreasureRaider4.NewTreasureRaiderHandler4;
import com.guaji.game.module.activity.newTreasureRaider4.NewTreasureRaiderInfoHandler4;
import com.guaji.game.module.activity.newTreasureRaider4.NewTreasureRaiderStatus4;
import com.guaji.game.module.activity.newUR.NewURHandler;
import com.guaji.game.module.activity.newUR.NewURInfoHandler;
import com.guaji.game.module.activity.newUR.NewURManager;
import com.guaji.game.module.activity.newUR.NewURStatus;
import com.guaji.game.module.activity.newWeekCard.NewWeekCardAwardGetHandler;
import com.guaji.game.module.activity.newWeekCard.NewWeekCardInfoHandler;
import com.guaji.game.module.activity.newWeekCard.NewWeekCardStatus;
import com.guaji.game.module.activity.obon.ObonHandler;
import com.guaji.game.module.activity.obon.ObonManager;
import com.guaji.game.module.activity.obon.ObonStatus;
import com.guaji.game.module.activity.princeDevils.PrinceDevilsPanelInfoHandler;
import com.guaji.game.module.activity.princeDevils.PrinceDevilsScoreHandler;
import com.guaji.game.module.activity.princeDevils.PrinceDevilsScorePanelHandler;
import com.guaji.game.module.activity.princeDevils.PrinceDevilsSearchHandler;
import com.guaji.game.module.activity.rankGift.RankGiftInfoHandler;
import com.guaji.game.module.activity.recharge.AccRechargeAwardHandler;
import com.guaji.game.module.activity.recharge.AccRechargeInfoHandler;
import com.guaji.game.module.activity.recharge.AccRechargeStatus;
import com.guaji.game.module.activity.recharge.ContinueMoneyRechargeStatus;
import com.guaji.game.module.activity.recharge.ContinueRechargeAwardHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeDays131AwardHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeDays131InfoHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeDays131Status;
import com.guaji.game.module.activity.recharge.ContinueRechargeInfoHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeMoneyAwardHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeMoneyInfoHandler;
import com.guaji.game.module.activity.recharge.ContinueRechargeStatus;
import com.guaji.game.module.activity.recharge.RechargeDoubleHandler;
import com.guaji.game.module.activity.recharge.RechargeRebateAwardHanlder;
import com.guaji.game.module.activity.recharge.RechargeRebateInfoHanlder;
import com.guaji.game.module.activity.recharge.RechargeRebateStatus;
import com.guaji.game.module.activity.recharge.SingleRechargeAwardHandler;
import com.guaji.game.module.activity.recharge.SingleRechargeInfoHandler;
import com.guaji.game.module.activity.recharge.SingleRechargeStatus;
import com.guaji.game.module.activity.releaseUR.ReleaseURHandler;
import com.guaji.game.module.activity.releaseUR.ReleaseURInfoHandler;
import com.guaji.game.module.activity.releaseUR.ReleaseURLotteryHandler;
import com.guaji.game.module.activity.releaseUR.ReleaseURResetHandler;
import com.guaji.game.module.activity.releaseUR.ReleaseURStatus;
import com.guaji.game.module.activity.releaseUR2.ReleaseURHandler2;
import com.guaji.game.module.activity.releaseUR2.ReleaseURInfoHandler2;
import com.guaji.game.module.activity.releaseUR2.ReleaseURLotteryHandler2;
import com.guaji.game.module.activity.releaseUR2.ReleaseURStatu2;
import com.guaji.game.module.activity.roulette.RouletteCreditsExchangeHandler;
import com.guaji.game.module.activity.roulette.RouletteInfoHandler;
import com.guaji.game.module.activity.roulette.RouletteRotateHandler;
import com.guaji.game.module.activity.salePacket.SalePacketGetHandler;
import com.guaji.game.module.activity.salePacket.SalePacketInfoHandler;
import com.guaji.game.module.activity.salePacket.SalePacketStatus;
import com.guaji.game.module.activity.shoot.ShootActivityInfo;
import com.guaji.game.module.activity.shoot.ShootActivityRequestHandler;
import com.guaji.game.module.activity.shoot.ShootPanelInfoHandler;
import com.guaji.game.module.activity.starEvaluation.StarEvaluationHandler;
import com.guaji.game.module.activity.taxi.ExchangeTaxiCodeHandler;
import com.guaji.game.module.activity.taxi.TaxiCodeInfoHandler;
import com.guaji.game.module.activity.timeLimit.PersonalTimeLimitStatus;
import com.guaji.game.module.activity.timeLimit.TimeLimitBuyHandler;
import com.guaji.game.module.activity.timeLimit.TimeLimitManager;
import com.guaji.game.module.activity.timeLimit.TimeLimitPurchaseInfoHandler;
import com.guaji.game.module.activity.treasureRaider.TreasureRaiderInfoHandler;
import com.guaji.game.module.activity.treasureRaider.TreasureRaiderSearchHandler;
import com.guaji.game.module.activity.treasureRaider.TreasureRaiderStatus;
import com.guaji.game.module.activity.turntable.TurntableExchangeHandler;
import com.guaji.game.module.activity.turntable.TurntableExchangeSyncHandler;
import com.guaji.game.module.activity.turntable.TurntableHandler;
import com.guaji.game.module.activity.turntable.TurntableManager;
import com.guaji.game.module.activity.turntable.TurntableStatus;
import com.guaji.game.module.activity.vipPackage.VipPackageGetHandler;
import com.guaji.game.module.activity.vipPackage.VipPackageInfoHandler;
import com.guaji.game.module.activity.vipPackage.VipPackageStatus;
import com.guaji.game.module.activity.vipwelfare.VipWelfareAwardHandler;
import com.guaji.game.module.activity.vipwelfare.VipWelfareInfoHandler;
import com.guaji.game.module.activity.vipwelfare.VipWelfareStatus;
import com.guaji.game.module.activity.wealthClub.WealthClubInfoHandler;
import com.guaji.game.module.activity.weekCard.WeekCardInfoHandler;
import com.guaji.game.module.activity.weekCard.WeekCardRewardHandler;
import com.guaji.game.module.activity.weekCard.WeekCardStatus;
import com.guaji.game.module.activity.welfareReward.WelfareRewardHandler;
import com.guaji.game.module.activity.welfareReward.WelfareRewardManager;
import com.guaji.game.module.activity.welfareReward.WelfareRewardStatus;
import com.guaji.game.module.activity.welfareRewardByRegDate.WelfareRewardByRegDateHandler;
import com.guaji.game.module.activity.welfareRewardByRegDate.WelfareRewardStatusByRegDate;
import com.guaji.game.module.activity.wordsexchange.WordsExchangeHandler;
import com.guaji.game.module.activity.wordsexchange.WordsExchangeInfoHandler;
import com.guaji.game.module.activity.wordsexchangespecial.WordsExchangeCycleHandler;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Activity.ExpeditionArmoryStage;
import com.guaji.game.protocol.Activity.HPExpeditionArmoryInfoRet;
import com.guaji.game.protocol.Activity.HPFirstRechargeGiftInfo;
import com.guaji.game.protocol.Activity.HPOpenActivitySyncS;
import com.guaji.game.protocol.Activity.HPVipWelfareInfoRet;
import com.guaji.game.protocol.Activity.HPWeekCardInfoRet;
import com.guaji.game.protocol.Activity.OpenActivity;
import com.guaji.game.protocol.Activity2.HPHaremInfo;
import com.guaji.game.protocol.Activity2.HPIosGitInfo;
import com.guaji.game.protocol.Activity2.HPRedPointInfo;
import com.guaji.game.protocol.Activity4.ConsumeWeekCardInfoRet;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.HPPlayerRegisterDay;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;

/**
 * 玩家活动基础组件（管理该玩家的总体活动信息）
 */
public class PlayerActivityModule extends PlayerModule {
    // 新手周期活动状态
    private Date lastDate;
    // 新手充值返利
    private int rebateActivityStatus;
    private boolean rebateIsSync;
    // 模块Tick周期
    private int tickIndex;
    private boolean sendFlag = false;
    /***
     * key 活动ID，value 时间戳 上次发送小红点的时间，避免在心跳方法中持续不断的给客户端推送小红点
     */
    private Map<Integer, Long> lastSendRedPointTimes = new HashMap<Integer, Long>();

    public PlayerActivityModule(Player player) {
        super(player);

        /**
         * 注册监听协议
         */
        listenProto(HP.code.GET_ACTIVITY_LIST_C);
        listenProto(HP.code.MONTHCARD_INFO_C, new MonthCardInfoHandler());
        listenProto(HP.code.MONTHCARD_AWARD_C, new MonthCardAwardGetHandler());
        listenProto(HP.code.CONSUME_MONTHCARD_INFO_C_VALUE, new ConMonthCardInfoHandler());
        listenProto(HP.code.CONSUME_MONTHCARD_AWARD_C_VALUE, new ConMonthCardAwardGetHandler());
        listenProto(HP.code.ACC_RECHARGE_INFO_C, new AccRechargeInfoHandler());

        listenProto(HP.code.GET_ACC_RECHARGE_AWARD_C, new AccRechargeAwardHandler());
        listenProto(HP.code.CONTINUE_RECHARGE_INFO_C, new ContinueRechargeInfoHandler());
        listenProto(HP.code.GET_CONTINUE_RECHARGE_AWARD_C, new ContinueRechargeAwardHandler());

        listenProto(HP.code.CONTINUE_RECHARGEMONEY_INFO_C, new ContinueRechargeMoneyInfoHandler());
        listenProto(HP.code.GET_CONTINUE_RECHARGEMONEY_AWARD_C, new ContinueRechargeMoneyAwardHandler());

        listenProto(HP.code.ACC_CONSUME_INFO_C, new AccConsumeInfoHandler());
        listenProto(HP.code.GET_ACC_CONSUME_AWARD_C, new AccConsumeAwardHandler());

        // 累计消费购买道具
        listenProto(HP.code.ACC_CONSUMEITEM_INFO_C, new AccConsumeItemInfoHandler());
        listenProto(HP.code.GET_ACC_CONSUMEITEM_AWARD_C, new AccConsumeItemAwardHandler());

        // 中秋换字活动
        listenProto(HP.code.WORDS_EXCHANGE_INFO_C, new WordsExchangeInfoHandler());
        listenProto(HP.code.WORDS_EXCHANGE_C, new WordsExchangeHandler());
        // 公测换字活动
        listenProto(HP.code.WORDS_EXHCNAGE_CYCLE_C, new WordsExchangeCycleHandler());
        // 双倍充值活动
        listenProto(HP.code.RECHARGE_DOUBLE_C_VALUE, new RechargeDoubleHandler());
        // 假日密宝
        listenProto(HP.code.HOLIDAY_TREASURE_C_VALUE, new HolidayTreasureHandler());
        // 注册天数
        listenProto(HP.code.REGISTER_CYCLE_INFO_C_VALUE, new HolidayTreasureHandler());
        // 每日单笔充值
        listenProto(HP.code.SINGLE_RECHARGE_INFO_C_VALUE, new SingleRechargeInfoHandler());
        listenProto(HP.code.SINGLE_RECHARGE_AWARD_C_VALUE, new SingleRechargeAwardHandler());
        // 充值返利
        listenProto(HP.code.RECHARGE_REBATE_INFO_C_VALUE, new RechargeRebateInfoHanlder());
        listenProto(HP.code.RECHARGE_REBATE_AWARD_C_VALUE, new RechargeRebateAwardHanlder());
        // 周卡
        listenProto(HP.code.WEEK_CARD_INFO_C_VALUE, new WeekCardInfoHandler());
        listenProto(HP.code.WEEK_CARD_REWARD_C_VALUE, new WeekCardRewardHandler());

        // 消耗型周卡(大月卡)
        listenProto(HP.code.CONSUME_WEEK_CARD_INFO_C_VALUE, new ConWeekCardInfoHandler());
        listenProto(HP.code.CONSUME_WEEK_CARD_REWARD_C_VALUE, new ConWeekCardRewardHandler());

        // vip福利
        listenProto(HP.code.VIP_WELFARE_INFO_C_VALUE, new VipWelfareInfoHandler());
        listenProto(HP.code.VIP_WELFARE_AWARD_C_VALUE, new VipWelfareAwardHandler());
        // 远征物资活动
        listenProto(HP.code.EXPEDITION_ARMORY_INFO_C_VALUE, new ExpeditionArmoryInfoHandler());
        listenProto(HP.code.EXPEDITION_ARMORY_RANKING_C_VALUE, new ExpeditionArmoryRankingHandler());
        listenProto(HP.code.EXPEDITION_ARMORY_USE_ITEM_C_VALUE, new ExpeditionArmoryUseItemHandler());
        listenProto(HP.code.EXPEDITION_ALL_SERVER_SCORE_INFO_C_VALUE, new ExpeditionArmoryAllServerHandler());

        // 累计登录活动
        listenProto(HP.code.ACC_LOGIN_INFO_C_VALUE, new AccLoginInfoHandler());
        listenProto(HP.code.ACC_LOGIN_AWARDS_C_VALUE, new AccLoginAwardsHandler());
        // 宝石工坊
        listenProto(HP.code.GEM_COMPOUND_INFO_C, new GemCompoundInfoHandler());
        listenProto(HP.code.GEM_COMPOUND_C, new GemCompoundHandler());
        // 疯狂转轮
        listenProto(HP.code.ROULETTE_INFO_C, new RouletteInfoHandler());
        listenProto(HP.code.ROULETTE_CREDITS_EXCHANGE_C, new RouletteCreditsExchangeHandler());
        listenProto(HP.code.ROULETTE_ROTATE_C, new RouletteRotateHandler());
        // 限时限购
        listenProto(HP.code.TIME_LIMIT_PURCHASE_INFO_C_VALUE, new TimeLimitPurchaseInfoHandler());
        listenProto(HP.code.TIME_LIMIT_BUY_C_VALUE, new TimeLimitBuyHandler());
        // 首充礼包
        listenProto(HP.code.FIRST_GIFTPACK_INFO_C_VALUE, new FirstGiftPackInfoHandler());
        listenProto(HP.code.FIRST_GIFTPACK_AWARD_C_VALUE, new FirstGiftPackAwardHandler());
        // 折扣礼包
        listenProto(HP.code.SALE_PACKET_INFO_C_VALUE, new SalePacketInfoHandler());
        listenProto(HP.code.SALE_PACKET_GET_AWARD_C_VALUE, new SalePacketGetHandler());
        // vip礼包
        listenProto(HP.code.NEW_WEEK_CARD_INFO_C_VALUE, new NewWeekCardInfoHandler());
        listenProto(HP.code.NEW_WEEK_CARD_GET_AWARD_C_VALUE, new NewWeekCardAwardGetHandler());
        // 周卡
        listenProto(HP.code.VIP_PACKAGE_INFO_C_VALUE, new VipPackageInfoHandler());
        listenProto(HP.code.VIP_PACKETAGE_GET_AWARD_C_VALUE, new VipPackageGetHandler());
        // 兑换活动
        listenProto(HP.code.EXCHANGE_INFO_C_VALUE, new ExchangeInfoHandler());
        listenProto(HP.code.DO_EXCHANGE_C_VALUE, new DoExchangeHandler());

        // 兑换所
        listenProto(HP.code.EXCHANGE_SHOP_INFO_C_VALUE, new ExchangeShopInfoHandler());
        listenProto(HP.code.DO_EXCHANGE_SHOP_C_VALUE, new DoExchangeShopHandler());
        listenProto(HP.code.ACTIVITY142_EXCHANGE_SHOP_INFO_C_VALUE, new activity142ExchangeShopInfoHandler());
        listenProto(HP.code.ACTIVITY142_DO_EXCHANGE_SHOP_C_VALUE, new activity142DoExchangeShopHandler());

        // 幸运宝箱
        listenProto(HP.code.LUCKBOX_INFO_C, new LuckyTreasureInfoHandler());
        listenProto(HP.code.LUCKBOX_EXCHANGE_C, new LuckyTreasureRewardHandler());
        // 邀请好友
        listenProto(HP.code.INVITE_FRIEND_INFO_C, new FriendInviteHandler());
        listenProto(HP.code.EXCHANGE_INVITE_CODE_C, new ExchangeInviteCodeHandler());
        listenProto(HP.code.INVITE_FRIEND_REWARD_C, new FriendInviteAwardHandler());
        // 出租车抵价（台湾大车队）
        listenProto(HP.code.TAXI_CODE_INFO_C, new TaxiCodeInfoHandler());
        listenProto(HP.code.EXCHANGE_TAXI_CODE_C, new ExchangeTaxiCodeHandler());
        // 5星好评
        StarEvaluationHandler handler = new StarEvaluationHandler();
        listenProto(HP.code.STAR_EVALUATION_INFO_C_VALUE, handler);
        listenProto(HP.code.STAR_EVALUATION_INFO_CLICK_C_VALUE, handler);
        listenProto(HP.code.STAR_EVALUATION_REWARD_C_VALUE, handler);
        // 雪地寻宝
//        listenProto(HP.code.SNOWFIELD_TREASURE_INFO_C, new SnowfieldTreasureInfoHandler());
//        listenProto(HP.code.SNOWFIELD_SEARCH_C, new SnowfieldSearchHandler());
//        listenProto(HP.code.SNOWFIELD_BUY_PHYC_C, new SnowfieldBuyPhycHandler());
//        listenProto(HP.code.SNOWFIELD_EXCHANGE_C, new SnowfieldExchangeHandler());
//        listenProto(HP.code.SET_CONTINUE_SEARCH_MODE_C, new SetContinueSearchModeHandler());
        // 夺宝奇兵
        listenProto(HP.code.TREASURE_RAIDER_INFO_C, new TreasureRaiderInfoHandler());
        listenProto(HP.code.TREASURE_RAIDER_SEARCH_C, new TreasureRaiderSearchHandler());
        // listenProto(HP.code.TREASURE_RAIDER_CONFIRM_C, new
        // TreasureRaiderBoxAwardsHandler());
        // 部族的宝藏
        CommendationTribeHandler commendationHandler = new CommendationTribeHandler();
        listenProto(HP.code.COMMENDATION_TRIBE_INFO_C_VALUE, commendationHandler);
        listenProto(HP.code.COMMENDATION_TRIBE_LUCK_C_VALUE, commendationHandler);
        // 财神献礼
        FortuneHandler fortuneHandler = new FortuneHandler();
        listenProto(HP.code.FORTUNE_INFO_C_VALUE, fortuneHandler);
        listenProto(HP.code.FORTUNE_REWARD_C_VALUE, fortuneHandler);
        // 财富俱乐部
        listenProto(HP.code.GOLD_CLUB_INFO_C_VALUE, new WealthClubInfoHandler());
        // 抢红包
        listenProto(HP.code.RED_ENVELOPE_INFO_C_VALUE, new RedEnvelopeInfoHandler());
        listenProto(HP.code.GIVE_RED_ENVELOPE_C_VALUE, new GiveRedEnvelopeHandler());
        listenProto(HP.code.GRAB_RED_ENVELOPE_C_VALUE, new GrabRedEnvelopeHandler());
        listenProto(HP.code.GRAB_FREE_RED_ENVELOPE_C_VALUE, new GrabFreeRedEnvelopeHandler());
        // 万家灯火
        listenProto(HP.code.FIND_TREASURE_INFO_C, new FindTreasureInfoHandler());
        listenProto(HP.code.FIND_TREASURE_SEARCH_C, new FindTreasureLightHandler());
        // 排行献礼
        listenProto(HP.code.RANK_GIFT_INFO_C, new RankGiftInfoHandler());
        // 终身卡
        listenProto(HP.code.FOREVER_CARD_INFO_C_VALUE, new ForeverCardInfoHandler());
        listenProto(HP.code.FOREVER_CARD_ACTIVATE_C_VALUE, new ForeverCardAvtivateHandler());
        listenProto(HP.code.FOREVER_CARD_GET_AWARD_C_VALUE, new ForeverCardDailyAwardHandler());
        // 捞鱼活动
        listenProto(HP.code.FISHING_INFO_C_VALUE, new GoldfishInfoHandler());
        listenProto(HP.code.CATCH_FISH_C_VALUE, new GoldfishFishingHandler());
        listenProto(HP.code.FISH_PREVIEW_C_VALUE, new GoldfishViewHandler());
        listenProto(HP.code.FISHING_RANK_C_VALUE, new GoldfishRankHandler());
        // 神器锻造
        listenProto(HP.code.EQUIP_BUILD_ACT_INFO_C_VALUE, new ForgingInfoHandler());
        listenProto(HP.code.EQUIP_BUILD_EVENT_C_VALUE, new ForgingHandler());

        // 气枪打靶
        listenProto(HP.code.SHOOT_PANEL_C_VALUE, new ShootPanelInfoHandler());
        listenProto(HP.code.SHOOT_START_C_VALUE, new ShootActivityRequestHandler());
        listenProto(HP.code.RED_POINT_LIST_C_VALUE);

        // 魔王宝藏
        listenProto(HP.code.PRINCE_DEVILS_PANEL_C_VALUE, new PrinceDevilsPanelInfoHandler());
        listenProto(HP.code.PRINCE_DEVILS_OPEN_C_VALUE, new PrinceDevilsSearchHandler());
        listenProto(HP.code.PRINCE_DEVILS_SCORE_PANEL_C_VALUE, new PrinceDevilsScorePanelHandler());
        listenProto(HP.code.PRINCE_DEVILS_SCORE_EXCHANGE_C_VALUE, new PrinceDevilsScoreHandler());

        // 成长基金
        listenProto(HP.code.GROWTH_FUND_INFO_C_VALUE, new GrowthFundInfoHandler());
        listenProto(HP.code.GROWTH_FUND_BUY_C_VALUE, new GrowthFundBuyHandler());
        listenProto(HP.code.GROWTH_FUND_GET_REWARD_C_VALUE, new GrowthFundAwardGetHandler());
        // 幸运福将
        listenProto(HP.code.LUCK_MERCENARY_C_VALUE, new LuckyMercenaryInfoHandler());
        // 老活动曝光统计
        listenProto(HP.code.ACTION_INTO_RECORD_C_VALUE, new ActivityClickRecordHandler());
        // 新夺宝奇兵
        listenProto(HP.code.NEW_TREASURE_RAIDER_INFO_C_VALUE, new NewTreasureRaiderInfoHandler());
        listenProto(HP.code.NEW_TREASURE_RAIDER_SEARCH_C_VALUE, new NewTreasureRaiderHandler());
        // 新夺宝奇兵复刻版
        listenProto(HP.code.NEW_TREASURE_RAIDER_INFO2_C_VALUE, new NewTreasureRaiderInfoHandler2());
        listenProto(HP.code.NEW_TREASURE_RAIDER_SEARCH2_C_VALUE, new NewTreasureRaiderHandler2());

        // 新夺宝奇兵复刻版3
        listenProto(HP.code.NEW_TREASURE_RAIDER_INFO3_C_VALUE, new NewTreasureRaiderInfoHandler3());
        listenProto(HP.code.NEW_TREASURE_RAIDER_SEARCH3_C_VALUE, new NewTreasureRaiderHandler3());

        // 新夺宝奇兵复刻版4
        listenProto(HP.code.NEW_TREASURE_RAIDER_INFO4_C_VALUE, new NewTreasureRaiderInfoHandler4());
        listenProto(HP.code.NEW_TREASURE_RAIDER_SEARCH4_C_VALUE, new NewTreasureRaiderHandler4());

        // 聊天皮肤活动
        listenProto(HP.code.CHAT_SKIN_INFO_C_VALUE, new ChatSkinInfoHandler());
        listenProto(HP.code.CHAT_SKIN_BUY_C_VALUE, new ChatSkinBuyHandler());
        listenProto(HP.code.CHAT_SKIN_OWNED_INFO_C_VALUE, new ChatSkinOwnedInfoHandler());
        listenProto(HP.code.CHAT_SKIN_CHANGE_C_VALUE, new ChatSkinChangeHandler());
        listenProto(HP.code.CHAT_SKIN_CLEAR_RED_POINT_C_VALUE, new ChatSkinClearRedPointHandler());
        // 王的后宫活动
        listenProto(HP.code.SYNC_HAREM_C_VALUE, new HaremInfoHandler());
        listenProto(HP.code.HAREM_DRAW_C_VALUE, new HaremHandler());
        listenProto(HP.code.HAREM_PANEL_INFO_C_VALUE, new HaremScorePanelInfoHandler());
        listenProto(HP.code.HAREM_EXCHANGE_C_VALUE, new HaremExchangeRequestHandler());
        // 等级折扣礼包
        listenProto(HP.code.DISCOUNT_GIFT_INFO_C_VALUE, new DiscountGiftInfoHandler());
        listenProto(HP.code.DISCOUNT_GIFT_GET_REWARD_C_VALUE, new DiscountGiftGetRewardHandler());
        // 万能碎片兑换活动
        listenProto(HP.code.SYNC_FRAGMENT_EXCHANGE_C_VALUE, new FragmentExchangeInfoHandler());
        listenProto(HP.code.FRAGMENT_EXCHANGE_C_VALUE, new FragmentExchangeHandler());
        // 仙女的保佑（女神的回馈、仙女の加護）累计充值活动
        listenProto(HP.code.SYNC_FAIRY_BLESS_C_VALUE, new FairyBlessInfoHandler());
        listenProto(HP.code.FAIRY_BLESS_C_VALUE, new FairyBlessHandler());
        // 少女的邂逅
        listenProto(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, new MaidenEncounterHandler());
        listenProto(HP.code.SYNC_MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, new MaidenEncounterExchangeInfoHandler());
        listenProto(HP.code.MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, new MaidenEncounterExchangeHandler());
        // 鬼节活动
        listenProto(HP.code.OBON_C_VALUE, new ObonHandler());
        // 天降元宝活动
        listenProto(HP.code.WELFARE_REWARD_C_VALUE, new WelfareRewardHandler());
        // 新手UR活动
        listenProto(HP.code.NEW_UR_INFO_C_VALUE, new NewURInfoHandler());
        listenProto(HP.code.NEW_UR_SEARCH_C_VALUE, new NewURHandler());
        // 去除特殊活动红点的请求
        listenProto(HP.code.REMOVE_SPECIAL_RED_POINT_VALUE, new RemoveRedPointHandler());
        // 大转盘活动
        listenProto(HP.code.TURNTABLE_C_VALUE, new TurntableHandler());
        listenProto(HP.code.SYNC_TURNTABLE_EXCHANGE_C_VALUE, new TurntableExchangeSyncHandler());
        listenProto(HP.code.TURNTABLE_EXCHANGE_C_VALUE, new TurntableExchangeHandler());
        // 万圣节活动
        listenProto(HP.code.HALLOWEEN_C_VALUE, new HalloweenHandler());
        // UR抽卡
        listenProto(HP.code.RELEASE_UR_INFO_C_VALUE, new ReleaseURInfoHandler());
        listenProto(HP.code.RELEASE_UR_DRAW_C_VALUE, new ReleaseURHandler());
        listenProto(HP.code.RELEASE_UR_RESET_C_VALUE, new ReleaseURResetHandler());
        listenProto(HP.code.RELEASE_UR_LOTTERY_C_VALUE, new ReleaseURLotteryHandler());

        // 登陆签到活动
        listenProto(HP.code.ACC_LOGIN_SIGNED_INFO_C_VALUE, new AccLoginSignedInfoHandler());
        listenProto(HP.code.ACC_LOGIN_SIGNED_AWARD_C_VALUE, new AccLoginSignedAwardsHandler());
        listenProto(HP.code.ACC_LOGIN_SIGNED_OPENCHEST_C_VALUE, new AccLoginSignedOpenChestHandler());

        // UR抽卡复刻版
        listenProto(HP.code.RELEASE_UR_INFO2_C_VALUE, new ReleaseURInfoHandler2());
        listenProto(HP.code.RELEASE_UR_DRAW2_C_VALUE, new ReleaseURHandler2());
        listenProto(HP.code.RELEASE_UR_LOTTERY2_C_VALUE, new ReleaseURLotteryHandler2());

        // UR抽卡
        listenProto(HP.code.RELEASE_UR_INFO3_C_VALUE, new ReleaseURInfoHandler121());
        listenProto(HP.code.RELEASE_UR_DRAW3_C_VALUE, new ReleaseURHandler121());
        listenProto(HP.code.RELEASE_UR_RESET3_C_VALUE, new ReleaseURResetHandler121());
        listenProto(HP.code.RELEASE_UR_LOTTERY3_C_VALUE, new ReleaseURLotteryHandler121());

        // 活跃度达标活动
        listenProto(HP.code.ACTIVECOMPLIANCE_INFO_C_VALUE, new ActiveComplianceInfoHandler());
        listenProto(HP.code.ACTIVECOMPLIANCE_AWARD_C_VALUE, new ActiveComplianceHandler());

        // UR抽卡复刻版
        listenProto(HP.code.ACTIVITY123_UR_INFO_C_VALUE, new Activity123InfoHandler());
        listenProto(HP.code.ACTIVITY123_UR_DRAW_C_VALUE, new Activity123Handler());
        listenProto(HP.code.ACTIVITY123_UR_LOTTERY_C_VALUE, new Activity123LotteryHandler());

        // 新充值返利抽奖活动抽奖
        listenProto(HP.code.ACTIVITY124_RECHARGE_RETURN_INFO_C_VALUE, new Activity124InfoHandler());
        listenProto(HP.code.ACTIVITY124_RECHARGE_RETURN_LOTTERY_C_VALUE, new Activity124LotteryHandler());

        // 武器屋活动
        listenProto(HP.code.ACTIVITY125_WEAPON_INFO_C_VALUE, new Activity125InfoHandler());
        listenProto(HP.code.ACTIVITY125_WEAPON_START_C_VALUE, new Activity125StartHandler());

        // 新用户许愿池
        listenProto(HP.code.WELFAREBYREGDATE_REWARD_C, new WelfareRewardByRegDateHandler());

        // 抽卡积分排行活动
        listenProto(HP.code.ACTIVITY128_UR_RANK_INFO_C_VALUE, new Activity128InfoHandler());
        listenProto(HP.code.ACTIVITY128_UR_RANK_LOTTERY_C_VALUE, new Activity128LotteryHandler());
        listenProto(HP.code.ACTIVITY128_UR_RANK_RANK_C_VALUE, new Activity128RankHandler());
        listenProto(HP.code.ACTIVITY128_UR_RANK_BOX_C_VALUE, new Activity128BoxHandler());

        // 等级礼包活动
        listenProto(HP.code.ACTIVITY132_LEVEL_GIFT_INFO_C_VALUE, new Activity132InfoHandler());
        listenProto(HP.code.ACTIVITY132_LEVEL_GIFT_BUY_C_VALUE, new Activity132BuyHandler());

        // 周末礼包活动
        listenProto(HP.code.ACTIVITY134_WEEKEND_GIFT_INFO_C_VALUE, new Activity134InfoHandler());
        listenProto(HP.code.ACTIVITY134_WEEKEND_GIFT_LOTTERY_C_VALUE, new Activity134LotteryHandler());
        listenProto(HP.code.ACTIVITY134_WEEKEND_GIFT_GET_C_VALUE, new Activity134GetHandler());

        // 老虎机返利活动
        listenProto(HP.code.ACTIVITY137_SLOT_RETURN_INFO_C_VALUE, new Activity137InfoHandler());
        listenProto(HP.code.ACTIVITY137_SLOT_RETURN_LOTTERY_C_VALUE, new Activity137LotteryHandler());
        
        
        // 138 h
        listenProto(HP.code.ACTIVITY138_RAIDER_INFO_C_VALUE, new NewTreasureRaiderInfoHandler139());
        listenProto(HP.code.ACTIVITY138_RAIDER_SEARCH_C_VALUE, new NewTreasureRaiderHandler139());
        
        //俄罗斯轮盘
        listenProto(HP.code.ACTIVITY140_DISHWHEEL_LOTTERY_C, new Activity140LotteryHandler());
        //大富翁
        listenProto(HP.code.ACTIVITY141_C_VALUE, new Activity141RichManHandler());
        //海盜寶箱
        listenProto(HP.code.ACTIVITY143_C_VALUE, new Activity143PirateHandler());
        //性奴小學堂
        listenProto(HP.code.ACTIVITY144_C_VALUE, new Activity144LittleTestHandler());
        
        // 精選召喚
        listenProto(HP.code.ACTIVITY146_CHOSEN_INFO_C_VALUE, new Activity146InfoHandler());
        listenProto(HP.code.ACTIVITY146_CHOSEN_DRAW_C_VALUE, new Activity146Handler());
        
        // 許願輪
        listenProto(HP.code.ACTIVITY147_WISHING_INFO_C, new Activity147InfoHandler());
        listenProto(HP.code.ACTIVITY147_WISHING_DRAW_C, new Activity147Handler());
        
        // 小瑪莉
        listenProto(HP.code.ACTIVITY148_C_VALUE, new Activity148Handler());
        
        // 關卡禮包活動
        listenProto(HP.code.ACTIVITY151_STAGE_GIFT_INFO_C_VALUE, new Activity151InfoHandler());
        listenProto(HP.code.ACTIVITY151_STAGE_GIFT_BUY_C_VALUE, new Activity151BuyHandler());
        // 英雄劇情禮包
        listenProto(HP.code.ACTIVITY152_C_VALUE, new Activity152Handler());
        // 排行榜禮物
        listenProto(HP.code.ACTIVITY153_C_VALUE, new Activity153Handler());
        // 精靈召喚
        listenProto(HP.code.ACTIVITY154_C_VALUE, new Activity154Handler());
        // 登入領十抽
        listenProto(HP.code.ACTIVITY157_C_VALUE, new Activity157Handler());
        // 種族召喚
        listenProto(HP.code.ACTIVITY158_C_VALUE, new Activity158Handler());
        // 每日累積VIP點獎勵
        listenProto(HP.code.ACTIVITY159_VIP_POINT_C, new Activity159Handler());
        // 累積儲值獎勵
        listenProto(HP.code.NP_CONTINUE_RECHARGE_MONEY_C_VALUE, new Activity160Handler());
        // 收費簽到
        listenProto(HP.code.SUPPORT_CALENDAR_ACTION_C, new Activity161Handler());
        // 成長獎勵LV
        listenProto(HP.code.ACTIVITY162_Growth_LV_C, new Activity162Handler());
        // 成長獎勵CH
        listenProto(HP.code.ACTIVITY163_Growth_CH_C, new Activity163Handler());
        // 成長獎勵TW
        listenProto(HP.code.ACTIVITY164_Growth_TW_C, new Activity164Handler());
        
        //挖礦活動
        listenProto(HP.code.ACTIVITY165_MINING_C, new Activity165Handler());
    	// 友情召喚
        listenProto(HP.code.ACTIVITY166_CALL_OF_FRIENDSHIP_INFO_C, new Activity166InfoHandler());
        listenProto(HP.code.ACTIVITY166_CALL_OF_FRIENDSHIP_DRAW_C, new Activity166Handler());
        //首儲免費召喚1500抽
        listenProto(HP.code.ACTIVITY167_FREE_SUMMON_C,new Activity167Handler());
        // 特權
        listenProto(HP.code.ACTIVITY168_SUBSCRIPTION_C,new Activity168Handler());
        
        // 彈跳禮包活動169
        listenProto(HP.code.ACTIVITY169_ACTIVITY_GIFT_INFO_C, new Activity169InfoHandler());
        listenProto(HP.code.ACTIVITY169_ACTIVITY_GIFT_VERIFY_C, new Activity169BuyHandler());
        
        // 彈跳禮包活動170
        listenProto(HP.code.ACTIVITY170_ACTIVITY_GIFT_INFO_C, new Activity170InfoHandler());
        listenProto(HP.code.ACTIVITY170_ACTIVITY_GIFT_VERIFY_C, new Activity170BuyHandler());
        
        // PICK UP召喚172
        listenProto(HP.code.ACTIVITY172_PICKUP_INFO_C, new Activity172InfoHandler());
        listenProto(HP.code.ACTIVITY172_PICKUP_DRAW_C, new Activity172Handler());
        
        // 新角召喚173
        listenProto(HP.code.ACTIVITY173_NEWROLE_INFO_C, new Activity173InfoHandler());
        listenProto(HP.code.ACTIVITY173_NEWROLE_DRAW_C, new Activity173Handler());
        
        // 壁尻活動
        listenProto(HP.code.ACTIVITY175_GLORY_HOLE_C, new Activity175Handler());
        // 活動兌換所
        listenProto(HP.code.ACTIVITY176_ACTIVITY_EXCHANGE_C, new Activity176ExchangeHandler());
        // 闖關失敗禮包 177
        listenProto(HP.code.ACTIVITY177_FAILED_GIFT_C, new Activity177Handler());
        // 專武召喚
        listenProto(HP.code.ACTIVITY178_CALL_OF_EQUIP_INFO_C, new Activity178InfoHandler());
        listenProto(HP.code.ACTIVITY178_CALL_OF_EQUIP_DRAW_C, new Activity178Handler());
        //　階段禮包
        listenProto(HP.code.ACTIVITY179_STEP_GIFT_C, new Activity179Handler());
        
        //免費召喚1500抽
        listenProto(HP.code.ACTIVITY180_FREE_SUMMON_C,new Activity180Handler());

        // 彈跳禮包活動181
        listenProto(HP.code.ACTIVITY181_ACTIVITY_GIFT_INFO_C, new Activity181InfoHandler());
        listenProto(HP.code.ACTIVITY181_ACTIVITY_GIFT_VERIFY_C, new Activity181BuyHandler());
        // 彈跳禮包活動182
        listenProto(HP.code.ACTIVITY182_ACTIVITY_GIFT_INFO_C, new Activity182InfoHandler());
        listenProto(HP.code.ACTIVITY182_ACTIVITY_GIFT_VERIFY_C, new Activity182BuyHandler());
        // 彈跳禮包活動183
        listenProto(HP.code.ACTIVITY183_ACTIVITY_GIFT_INFO_C, new Activity183InfoHandler());
        listenProto(HP.code.ACTIVITY183_ACTIVITY_GIFT_VERIFY_C, new Activity183BuyHandler());
        
        // 彈跳禮包活動184
        listenProto(HP.code.ACTIVITY184_ACTIVITY_GIFT_INFO_C, new Activity184InfoHandler());
        listenProto(HP.code.ACTIVITY184_ACTIVITY_GIFT_VERIFY_C, new Activity184BuyHandler());
        // 彈跳禮包活動185
        listenProto(HP.code.ACTIVITY185_ACTIVITY_GIFT_INFO_C, new Activity185InfoHandler());
        listenProto(HP.code.ACTIVITY185_ACTIVITY_GIFT_VERIFY_C, new Activity185BuyHandler());
        // 彈跳禮包活動186
        listenProto(HP.code.ACTIVITY186_ACTIVITY_GIFT_INFO_C, new Activity186InfoHandler());
        listenProto(HP.code.ACTIVITY186_ACTIVITY_GIFT_VERIFY_C, new Activity186BuyHandler());
        
        // 加強彈跳禮包活動187
        listenProto(HP.code.ACTIVITY187_MAXJUMP_GIFT_C, new Activity187Handler());
        
        // 累積返利900召喚活動190
        listenProto(HP.code.ACTIVITY190_STEP_SUMMON_C, new Activity190Handler());
        
        // 循環關卡活動191(成就及日常任務)
        listenProto(HP.code.ACTIVITY191_CYCLE_STAGE_C, new Activity191Handler());
        
        // 累儲累消累充192
        listenProto(HP.code.ACTIVITY192_RECHARGE_BOUNCE_C, new Activity192Handler());
        
        // 單人強敵193
        listenProto(HP.code.ACTIVITY193_SINGLE_BOSS_C, new Activity193Handler());
        
        // 賽季爬塔194
        listenProto(HP.code.ACTIVITY194_SEASON_TOWER_C, new Activity194Handler());
        
        // 循環關卡復刻活動196(成就及日常任務)
        listenProto(HP.code.ACTIVITY196_CYCLE_STAGE_C, new Activity196Handler());
        
        // 超級召喚197
        listenProto(HP.code.ACTIVITY197_SUPER_PICKUP_INFO_C, new Activity197InfoHandler());
        listenProto(HP.code.ACTIVITY197_SUPER_PICKUP_DRAW_C, new Activity197Handler());
        /**
         * 注册监听消息
         */
        listenMsg(GsConst.MsgType.MONTH_CARD_SUC);
        listenMsg(GsConst.MsgType.ACTIVITY_LIST_CHANGE);
        listenMsg(GsConst.MsgType.ALL_ACTIVITY_AWARDS_GOT);
        listenMsg(GsConst.MsgType.RECHARGE_DAYS_INCREASE);
        listenMsg(GsConst.MsgType.ACC_CONSUME_INCREASE);
        listenMsg(GsConst.MsgType.WEEK_CARD_SUC);
        listenMsg(GsConst.MsgType.EXPEDITION_ARMORY_STATUS_CHANGE);
        listenMsg(GsConst.MsgType.FIRST_GIFT_GET_SUCCESS);
        listenMsg(GsConst.MsgType.SHOOT_ACTIVITY);
        listenMsg(GsConst.MsgType.NEW_UR_ACTIVITY);
        listenMsg(GsConst.MsgType.CONSUME_WEEK_CARD_SUC);
        listenMsg(GsConst.MsgType.CONSUME_MONTH_CARD_SUC);
        
		// for activity daily
        listenMsg(GsConst.MsgType.DailyQuestMsg.LOGIN_DAY);
        listenMsg(GsConst.MsgType.DailyQuestMsg.FAST_FIGHT);
		
		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL);
		listenMsg(GsConst.MsgType.DailyQuestMsg.EQUIP_FORGE);
		listenMsg(GsConst.MsgType.DailyQuestMsg.TAKE_FIGHT_AWARD);
		listenMsg(GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP);
		listenMsg(GsConst.MsgType.DailyQuestMsg.GLORYHOLE_JOINTIMES);
		listenMsg(GsConst.MsgType.DailyQuestMsg.DAILY_POINT);
		listenMsg(GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES);
		
//		listenMsg(GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.NOR_MISSION_WIN);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_STAR);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.JING_JI_CHANG_FIGHT);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.EQUIP_ENHANCE);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.SMELT_EQUIP);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.ROLE_EXPEDITION_COUNT);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.BADGE_FUSION);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.MONEY_COLLETION);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.WORLD_SPEAK) ;
//		listenMsg(GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP);
//		listenMsg(GsConst.MsgType.DailyQuestMsg.CALL_HERO);

        // 武器召唤师
        listenProto(HP.code.ACTIVITY127_UR_INFO_C_VALUE, new Activity127InfoHandler());
        listenProto(HP.code.ACTIVITY127_UR_DRAW_C_VALUE, new Activity127Handler());

        // 累积充值天数活动（不限新手）131
        listenProto(HP.code.CONTINUE_RECHARGE131_INFO_C, new ContinueRechargeDays131InfoHandler());
        listenProto(HP.code.GET_CONTINUE_RECHARGE131_AWARD_C, new ContinueRechargeDays131AwardHandler());
    }

    @Override
    public boolean onMessage(Msg msg) {
        if (msg.getMsg() == GsConst.MsgType.MONTH_CARD_SUC) {
            /*
             * MonthCardStatus monthCardStatus = msg.getParam(0); if(monthCardStatus !=
             * null) { if(monthCardStatus.getMonthActiveCount() > 1) { player.sendStatus(0,
             * Status.error.MONTH_CARD_BUY_CONTINUE_SUC_VALUE); } else {
             * player.sendStatus(0, Status.error.MONTH_CARD_BUY_SUC_VALUE); } }
             */
            return true;
        } else if (msg.getMsg() == GsConst.MsgType.ACTIVITY_LIST_CHANGE
                || msg.getMsg() == GsConst.MsgType.ALL_ACTIVITY_AWARDS_GOT
                || msg.getMsg() == GsConst.MsgType.RECHARGE_DAYS_INCREASE
                || msg.getMsg() == GsConst.MsgType.ACC_CONSUME_INCREASE
                || msg.getMsg() == GsConst.MsgType.FIRST_GIFT_GET_SUCCESS
                || msg.getMsg() == GsConst.MsgType.NEW_UR_ACTIVITY || msg.getMsg() == GsConst.MsgType.SHOOT_ACTIVITY) {
            syncActivityList();
            return true;
        } else if (msg.getMsg() == GsConst.MsgType.WEEK_CARD_SUC) {
            WeekCardStatus weekCardStatus = msg.getParam(0);
            if (weekCardStatus != null) {
                WeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(WeekCardCfg.class,
                        weekCardStatus.getCurrentActiveCfgId());
                if (weekCardCfg != null) {
                    // player.sendStatus(0, Status.error.WEEK_CARD_SUC_VALUE);

                    int activityId = Const.ActivityId.WEEK_CARD_VALUE;
                    ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
                    if (activityTimeCfg == null) {
                        player.sendError(HP.code.WEEK_CARD_INFO_C_VALUE, Status.error.ACTIVITY_CLOSE);
                        return true;
                    }

                    HPWeekCardInfoRet.Builder ret = BuilderUtil.genWeekCardInfo(weekCardStatus, activityTimeCfg);
                    player.sendProtocol(Protocol.valueOf(HP.code.WEEK_CARD_INFO_S_VALUE, ret));

                }
            }
            return true;
        } else if (GsConst.MsgType.CONSUME_WEEK_CARD_SUC == msg.getMsg()) {

            int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;

            ConWeekCardStatus weekCardStatus = msg.getParam(0);
            if (weekCardStatus != null) {
                ConsumeWeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(ConsumeWeekCardCfg.class,
                        weekCardStatus.getCurrentActiveCfgId());
                if (weekCardCfg != null) {

                    ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
                    if (activityTimeCfg == null) {
                        player.sendError(HP.code.CONSUME_WEEK_CARD_INFO_S_VALUE, Status.error.ACTIVITY_CLOSE);
                        return true;
                    }
                    ConsumeWeekCardInfoRet.Builder ret = BuilderUtil.genConsumeWeekCardInfo(weekCardStatus,
                            activityTimeCfg);

                    // HPWeekCardInfoRet.Builder ret = BuilderUtil.genWeekCardInfo(weekCardStatus,
                    // activityTimeCfg);
                    player.sendProtocol(Protocol.valueOf(HP.code.CONSUME_WEEK_CARD_INFO_S_VALUE, ret));

                }
            }
            return true;
            // 周卡充值成功
        } else if (GsConst.MsgType.CONSUME_MONTH_CARD_SUC == msg.getMsg()) {
            // 月卡充值成功
            int activityId = Const.ActivityId.CONSUME_MONTH_CARD_VALUE;
            ConMonthCardStatus monthCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, 0,
                    ConMonthCardStatus.class);
            if (monthCardStatus == null) {
                player.sendError(HP.code.CONSUME_MONTHCARD_INFO_S_VALUE, Status.error.ACTIVITY_CLOSE);
                return true;
            }

            player.sendProtocol(Protocol.valueOf(HP.code.CONSUME_MONTHCARD_INFO_S_VALUE,
                    BuilderUtil.genConMonthCardStatus(monthCardStatus)));

            return true;
        } else if (msg.getMsg() == GsConst.MsgType.EXPEDITION_ARMORY_STATUS_CHANGE) {
            syncExpeditionArmoryStatus();
            return true;       
        } else if ((msg.getMsg() == GsConst.MsgType.DailyQuestMsg.LOGIN_DAY)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.FAST_FIGHT)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.EQUIP_FORGE)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.TAKE_FIGHT_AWARD)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.GLORYHOLE_JOINTIMES)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.DAILY_POINT)||
        		(msg.getMsg() == GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES)) {
        	
        	if (msg.getMsg() != GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES) {
        		onGloryHoleDailyCount(msg);
        	}
        	
        	if (msg.getMsg() != GsConst.MsgType.DailyQuestMsg.GLORYHOLE_JOINTIMES) {
        		onCycleStageDailyCount(msg);
        	}
        	return true;
        }
        return super.onMessage(msg);
    }

    /**
     * 玩家上线处理
     *
     * @return
     */
    @Override
    protected boolean onPlayerLogin() {
        player.getPlayerData().loadActivity();
        player.getPlayerData().loadChatSkinEntity();
        player.getPlayerData().loadHaremEntity();
        player.getPlayerData().loadMaidenEncounterEntity();
        this.tickIndex = 0;
        this.lastDate = GuaJiTime.getCalendar().getTime();
        this.rebateIsSync = false;

        // 充值返利活动状态2初始
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.RECHARGE_REBATE2_VALUE);
        this.rebateIsSync = true;
        if (timeCfg != null) {
            this.rebateIsSync = timeCfg.isEnd();
        }
        // 充值返利活动状态1初始
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        this.rebateActivityStatus = ActivityUtil.calcRechargeRebateActivityStatus(registerDate);

        // 刷新月卡
        refreshMonthCard();

        // 刷新周卡
        refreshNewWeekCard();

        // 刷新累计登录
        refreshAccLoginStatus();

        // 获取充值返利抽奖活动彩票使用状态
        ActivityUtil.notifyActivity124Info(player);

        // 重置134活动数据
        Activity134Manager.restActivity134Status(player);
        // 推送134周末礼包info
        Activity134Manager.pushActivity134Info(player);

        // 同步老虎机状态
        ActivityUtil.notifyActivity137Info(player);
        // 俄罗斯轮盘
        ActivityUtil.notifyActivity140Info(player);
        
        return true;
    }

    /**
     * 玩家组装完成后活动数据同步
     *
     * @return
     */
    @Override
    protected boolean onPlayerAssemble() {
        syncActivityList();
        player.pushRegisterCycle();
        pushClientShowRedPoint();

        HPPlayerRegisterDay.Builder builder = HPPlayerRegisterDay.newBuilder();

        Date curDate = GuaJiTime.getCalendar().getTime();
        if (player.getPlayerData().getPlayerEntity().getCreateTime().getTime() > curDate.getTime()) {
            builder.setRegisterDay(0);
        } else {
            builder.setRegisterDay(
                    GuaJiTime.calcBetweenDays(player.getPlayerData().getPlayerEntity().getCreateTime(), curDate) + 1);
        }

        this.sendProtocol(Protocol.valueOf(HP.code.PLAYER_REGISTERDAY_S, builder));

//		sendVipWelfareStatus();
//		fixMonthActivityInfos();
//		sendFirstGiftPackStatus();//首充礼包
        return true;
    }

    /*
     * private void fixMonthActivityInfos() { //修复月卡的期数 MonthCardStatus
     * monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());
     * if(monthCardStatus != null) { // 捞取现在生效的月卡邮件 List<EmailEntity> emailEntities
     * = DBManager.getInstance().
     * query("from EmailEntity where playerId = ? and mailId = 7 and invalid = 0 order by id"
     * , player.getId());
     *
     * List<Integer> monthCardIds = monthCardStatus.getMonthCardCfgIds();
     * monthCardIds.clear(); int activeIdNum = emailEntities.size() % 30 == 0 ?
     * emailEntities.size() / 30 : emailEntities.size() / 30 + 1;
     *
     * for(int i=0;i<activeIdNum;i++) { monthCardIds.add(1); }
     *
     * if(emailEntities.size() > 0) { EmailEntity firtEmailEntity =
     * emailEntities.get(0); firtEmailEntity.convertData(); Calendar calendar =
     * Calendar.getInstance();
     * calendar.setTimeInMillis(firtEmailEntity.getEffectTime().getTime()); int
     * leftDays = Integer.valueOf(firtEmailEntity.getParamsList().get(0));
     * calendar.add(Calendar.DATE, (leftDays - 29));
     * monthCardStatus.setStartDate(calendar.getTime()); }else{
     * monthCardStatus.setStartDate(null); }
     * player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0);
     * } }
     */
    @Override
    public boolean onTick() {
        if (++tickIndex % 100 == 0) {
            // 充值返利活动状态切换
            onRebateStatusChange();
            // 检测显示限购状态
            TimeLimitManager.getInstance().refresh();

            this.refreshMonthCard();
            this.refreshNewWeekCard();

            checkShootRefreshTime();
            checkNewTreasureRaiderRP();
            checkNewTreasureRaider2RP();
            checkNewTreasureRaiderRP3();
            checkNewTreasureRaiderRP4();
            checkNewTreasureRaider139RP();
            checkNewURRedPoint();
            checkReleaseURRedPoint();
            checkActivty121RRedPoint();
            Date curDate = GuaJiTime.getCalendar().getTime();
            // 在线跨天
            if (!GuaJiTime.isSameDay(lastDate.getTime(), curDate.getTime())) {
                lastDate = curDate;
                player.pushRegisterCycle();
                pushClientShowRedPoint();

                // 重置134活动数据
                Activity134Manager.restActivity134Status(player);
                // 推送134周末礼包info
                Activity134Manager.pushActivity134Info(player);
                // 重置老虎机活动数据
                ActivityUtil.restActivity137Status(player.getPlayerData());
                // 同步老虎机状态
                ActivityUtil.notifyActivity137Info(player);
                // 重置俄罗斯轮盘
               ActivityUtil.restActivity140Status(player.getPlayerData(), false);
                // 同步俄罗斯轮盘
               ActivityUtil.notifyActivity140Info(player);
            }
            tickIndex = 0;
        }
        return true;
    }

    /**
     * 同步当前活动列表
     */
    private void syncActivityList() {
        HPOpenActivitySyncS.Builder ret = HPOpenActivitySyncS.newBuilder();
        // 当前服务器开放的所有活动
        List<OpenActivity.Builder> activityBuilderList = ActivityManager.getInstance()
                .getPersonalActiveActivityBuilderList(player);
        // 策划和运营特殊需求-某些活动需要根据个人状态提前关闭
        for (OpenActivity.Builder activityBuilder : activityBuilderList) {
            if (ActivityUtil.isCancelShowInClient(activityBuilder.getActivityId(), activityBuilder.getStageId(),
                    player.getPlayerData())) {
                continue;
            }
            ret.addActivity(activityBuilder);
        }
        ret.setVersion(1);
        sendProtocol(Protocol.valueOf(HP.code.OPEN_ACTIVITY_SYNC_S, ret));
    }

    /**
     * 同步阵营战状态切换
     */
    private void syncExpeditionArmoryStatus() {
        int activityId = Const.ActivityId.EXPEDITION_ARMORY_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        ExpeditionArmoryManager activityMan = ExpeditionArmoryManager.getInstance();
        ExpeditionArmoryEntity expeditionArmoryEntity = activityMan.getCurrentActiveExpeditionArmory();
        if (timeCfg == null || expeditionArmoryEntity == null) {
            return;
        }

        HPExpeditionArmoryInfoRet.Builder ret = HPExpeditionArmoryInfoRet.newBuilder();
        ret.setCurStage(expeditionArmoryEntity.getCurDonateStage());
        ret.setLeftTime(timeCfg.calcActivitySurplusTime());
        ExpeditionArmoryStatus expeditionArmoryStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityId, timeCfg.getStageId(), ExpeditionArmoryStatus.class);
        List<ExpeditionArmoryStage.Builder> stageBuilders = expeditionArmoryStatus.getAllStageStatusBuilder();
        for (ExpeditionArmoryStage.Builder stageBuilder : stageBuilders) {
            ret.addExpeditionArmoryStage(stageBuilder);
        }
        player.sendProtocol(Protocol.valueOf(HP.code.EXPEDITION_ARMORY_INFO_S_VALUE, ret));
    }

    /**
     * 协议响应，客户端请求活动列表
     *
     * @param protocol
     * @return
     */
    @Override
    public boolean onProtocol(Protocol protocol) {
        if (protocol.checkType(HP.code.GET_ACTIVITY_LIST_C)) {
            syncActivityList();
            return true;
        }

        if (protocol.checkType(HP.code.RED_POINT_LIST_C)) {
            pushClientShowRedPoint();
            return true;
        }
        return super.onProtocol(protocol);
    }

    /**
     * 刷新月卡状态
     */
    public void refreshMonthCard() {
        // refresh month card status
        MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());
        if (monthCardStatus != null) {
            if (monthCardStatus.refresh(player.getPlayerData())) {
                player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0, true);
            }
        }
    }

    /**
     * 刷新周卡状态
     */
    public void refreshWeekCard() {
        // refresh month card status
        // ActivityUtil.getWeekCardStatus(playerData)
        /*
         * WeekCardStatus weekCardStatus =
         * ActivityUtil.getWeekCardStatus(player.getPlayerData()); if (weekCardStatus !=
         * null) { if (weekCardStatus.refresh(player.getPlayerData())) {
         * player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0,
         * true); } }
         */

    }

    /**
     * 刷新周卡状态
     */
    public void refreshNewWeekCard() {
        // refresh month card status
        NewWeekCardStatus newWeekCardStatus = ActivityUtil.getNewWeekCardStatus(player.getPlayerData());
        if (newWeekCardStatus != null) {
            if (newWeekCardStatus.refreshNewWeekCard(player.getPlayerData())) {
                player.getPlayerData().updateActivity(Const.ActivityId.NEW_WEEK_CARD_VALUE, 0, true);
            }
        }
    }

    /**
     * 刷新累计登录活动状态
     */
    public void refreshAccLoginStatus() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg != null) {
            AccLoginStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                    timeCfg.getStageId(), AccLoginStatus.class);
            int lastLoginDays = status.getTotalLoginDays();
            int curLoginDays = status.refreshLoginDays();
            if (curLoginDays > lastLoginDays) {
                player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
            }
        }
    }

    /**
     * 充值返利活动状态切换
     */
    public void onRebateStatusChange() {
        // 按注册时间开放
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        int newStatus = ActivityUtil.calcRechargeRebateActivityStatus(registerDate);
        if (newStatus != this.rebateActivityStatus) {
            syncActivityList();
            this.rebateActivityStatus = newStatus;
        }

        // 按周期开放
        int activityId = Const.ActivityId.RECHARGE_REBATE2_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg != null && timeCfg.isEnd() && !rebateIsSync) {
            syncActivityList();
            this.rebateIsSync = true;
        }
    }

    /**
     * 发送vip福利活动状态信息给前端。提示红点用
     */
    public void sendVipWelfareStatus() {
        VipWelfareStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.VIP_WELFARE_VALUE, 0, VipWelfareStatus.class);
        if (status.getLastAwareTime() == null || !GuaJiTime.isSameDay(status.getLastAwareTime().getTime(),
                GuaJiTime.getCalendar().getTime().getTime())) {
            status.setAwareStatus(VipWelfareAwardHandler.NOTGET);
            player.getPlayerData().updateActivity(Const.ActivityId.VIP_WELFARE_VALUE, 0);
        }
        HPVipWelfareInfoRet.Builder builder = HPVipWelfareInfoRet.newBuilder();
        builder.setAwardStatus(status.getAwareStatus());
        player.sendProtocol(Protocol.valueOf(HP.code.VIP_WELFARE_INFO_S, builder));
    }

    // vip特典红点
    public void sendVipWelfareRedPoint(List<Integer> pointList) {
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.VIP_WELFARE_VALUE);
        if (timeCfg == null) {
            return;
        }

        VipWelfareStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.VIP_WELFARE_VALUE, 0, VipWelfareStatus.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint(player.getVipLevel())) {
            // 发红点
            pointList.add(Const.ActivityId.VIP_WELFARE_VALUE);
        }
        return;
    }

    // 首冲礼包红点
    public void sendFirstGiftRedPoint(List<Integer> pointList) {
        boolean isFirstRecharge = player.getPlayerData().getPlayerEntity().getRecharge() == 0 ? true : false;

        if (!isFirstRecharge) {
            FirstGiftPackStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                    Const.ActivityId.FIRST_GIFTPACK_VALUE, 0, FirstGiftPackStatus.class);
            if (status.getLastAwareTime() == null) {
                // 红点
                pointList.add(Const.ActivityId.FIRST_GIFTPACK_VALUE);

            }
        }

    }

    // 周卡红点
    public void sendNewWeekCardRedPoint(List<Integer> pointList) {
        NewWeekCardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.NEW_WEEK_CARD_VALUE, 0, NewWeekCardStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.NEW_WEEK_CARD_VALUE);

        }
    }

    // 月卡红点
    public void sendMonthCardRedPoint(List<Integer> pointList) {
        MonthCardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.MONTH_CARD_VALUE, 0, MonthCardStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.MONTH_CARD_VALUE);

        }
        return;
    }

    // 消耗型月卡红点
    public void sendConsumeMonthCardRedPoint(List<Integer> pointList) {
        ConMonthCardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0, ConMonthCardStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.CONSUME_MONTH_CARD_VALUE);

        }
        return;
    }

    // vip礼包红点
    public void sendVipPackageRedPoint(List<Integer> pointList) {
        VipPackageStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.VIP_PACKAGE_VALUE, 0, VipPackageStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint(player.getVipLevel())) {
            // 红点
            pointList.add(Const.ActivityId.VIP_PACKAGE_VALUE);

        }
        return;
    }

    // 射击活动红点
    public void sendShootRedPoint(List<Integer> pointList) {
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
        if (timeCfg == null) {
            return;
        }
        ShootActivityInfo shootInfo = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.SHOOT_ACTIVITY_VALUE, timeCfg.getStageId(), ShootActivityInfo.class);
        if (shootInfo == null) {
            return;
        }

        if (shootInfo.checkRefreshTimeCome(player)) {
            // 红点
            pointList.add(Const.ActivityId.SHOOT_ACTIVITY_VALUE);

            return;
        }

    }

    public void checkShootRefreshTime() {
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
        if (timeCfg == null) {
            return;
        }

        ShootActivityInfo shootInfo = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.SHOOT_ACTIVITY_VALUE, timeCfg.getStageId(), ShootActivityInfo.class);
        if (shootInfo == null) {
            return;
        }

        if (shootInfo.checkRefreshTimeCome(player)) {
            if (!sendFlag) {
                pushClientShowRedPointByID(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
                sendFlag = true;
            }
            return;
        } else {
            sendFlag = false;
        }

    }

    // 折扣礼包红点
    public void sendSalePacketRedPoint(List<Integer> pointList) {
        SalePacketStatus salePacketStatus = ActivityUtil.getSalePacketStatus(player.getPlayerData());

        if (salePacketStatus == null) {
            return;
        }

        if (salePacketStatus.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.SALE_PACKET_VALUE);

            return;
        }
    }

    // 连续充值红点
    public void sendContinueRechargeRedPoint(List<Integer> pointList) {
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.CONTINUE_RECHARGE_VALUE);
        if (timeCfg == null) {
            return;
        }
        ContinueRechargeStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.CONTINUE_RECHARGE_VALUE, timeCfg.getStageId(), ContinueRechargeStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.CONTINUE_RECHARGE_VALUE);
            return;
        }

    }

    /**
     * 连续充值红点(累积天数不限新手)
     */
    public void sendContinueRechargeDays131RedPoint(List<Integer> pointList) {
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.CONTINUE_RECHARGE131_VALUE);
        if (timeCfg == null) {
            return;
        }
        ContinueRechargeDays131Status status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.CONTINUE_RECHARGE131_VALUE, timeCfg.getStageId(), ContinueRechargeDays131Status.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.CONTINUE_RECHARGE131_VALUE);
            return;
        }

    }

    // 连续充值红点
    public void sendContinueRechargeMoneyRedPoint(List<Integer> pointList) {

        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE);
        if (timeCfg == null) {
            return;
        }
        ContinueMoneyRechargeStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE, timeCfg.getStageId(),
                ContinueMoneyRechargeStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE);
            return;
        }
    }

    // 充值返利红点
    public void sendRechargeRebateRedPoint(List<Integer> pointList) {
        RechargeRebateStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.RECHARGE_REBATE_VALUE, -1, RechargeRebateStatus.class);

        if (status == null) {
            return;
        }
        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.RECHARGE_REBATE_VALUE);

            return;
        }

    }

    // 每日累计充值红点
    public void sendAccRechargePoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE);
        if (cfg == null) {
            return;
        }
        AccRechargeStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE, cfg.getStageId(), AccRechargeStatus.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE);
            return;
        }
    }

    // 累计登录红点
    public void sendAccLoginPoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE);
        if (cfg == null) {
            return;
        }
        AccLoginStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE, cfg.getStageId(), AccLoginStatus.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE);
            return;
        }
    }

    // 每日单笔充值红点
    public void sendSingleRechargePoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SINGLE_RECHARGE_VALUE);
        if (cfg == null) {
            return;
        }
        SingleRechargeStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.SINGLE_RECHARGE_VALUE, cfg.getStageId(), SingleRechargeStatus.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint()) {
            // 红点
            pointList.add(Const.ActivityId.SINGLE_RECHARGE_VALUE);
            return;
        }
    }

    // 魔王宝藏红点（有免费次数时，推送红点）
    public void sendPrinceDevilsPoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.PRINCE_DEVILS_VALUE);
        if (cfg == null) {
            return;
        }

        if (!ActivityUtil.isActivityOpen(Const.ActivityId.PRINCE_DEVILS_VALUE)) {
            return;
        }

        PlayerPrinceDevilsEntity entity = player.getPlayerData().loadPrinceDevilsEntity();

        // 获取参数配置
        PrinceDevilsCostCfg config = ConfigManager.getInstance().getConfigByIndex(PrinceDevilsCostCfg.class, 0);
        if (config == null) {
            return;
        }

        // 超过刷新时间，有免费次数
        if (GuaJiTime.getSeconds() - entity.getFreeTime() >= config.getRefreshTime()) {
            // 红点
            pointList.add(Const.ActivityId.PRINCE_DEVILS_VALUE);
        }
    }

    // 夺宝骑兵红点（有免费次数时，推送红点）
    public void sendTreasurePoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.TREASURE_RAIDER_VALUE);
        if (cfg == null) {
            return;
        }

        if (!ActivityUtil.isActivityOpen(Const.ActivityId.TREASURE_RAIDER_VALUE)) {
            return;
        }

        TreasureRaiderStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.TREASURE_RAIDER_VALUE, cfg.getStageId(), TreasureRaiderStatus.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint(player.getVipLevel())) {
            // 红点
            pointList.add(Const.ActivityId.TREASURE_RAIDER_VALUE);
            return;
        }
    }

    // 成长基金红点（未购买，有奖励未领取时，推送红点）
    public void sendGrowthFundPoint(List<Integer> pointList) {

        GrowthFundStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.GROWTH_FUND_VALUE, 0, GrowthFundStatus.class);
        if (status == null) {
            return;
        }

        if (status.showRedPoint(player.getVipLevel(), player.getLevel())) {
            // 红点
            pointList.add(Const.ActivityId.GROWTH_FUND_VALUE);
            return;
        }
    }

    // 捞金鱼红点（有免费次数时，推送红点）
    public void sendGoldFishRedPoint(List<Integer> pointList) {
        ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeToEndCfg(Const.ActivityId.GOLD_FISH_VALUE);
        if (cfg == null) {
            return;
        }

        GoldfishStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.GOLD_FISH_VALUE,
                cfg.getStageId(), GoldfishStatus.class);
        if (status == null) {
            return;
        }

        if (status.isFirstTime() || status.getFreeTimes() > 0) {
            // 红点
            pointList.add(Const.ActivityId.GOLD_FISH_VALUE);
            return;
        }
    }

    private void pushClientShowRedPoint() {
        // 有时间改成多态
        List<Integer> pointList = new ArrayList<Integer>();

        sendVipWelfareRedPoint(pointList);
        sendFirstGiftRedPoint(pointList);
        sendNewWeekCardRedPoint(pointList);
        sendMonthCardRedPoint(pointList);
        sendVipPackageRedPoint(pointList);
        sendShootRedPoint(pointList);
        sendSalePacketRedPoint(pointList);
        sendContinueRechargeRedPoint(pointList);
        sendContinueRechargeDays131RedPoint(pointList);
        sendRechargeRebateRedPoint(pointList);
        sendAccRechargePoint(pointList);
        sendAccLoginPoint(pointList);
        sendSingleRechargePoint(pointList);
        sendPrinceDevilsPoint(pointList);
        sendTreasurePoint(pointList);
        sendGrowthFundPoint(pointList);
        // 累计消费小红点
        sendAccConsumeAwardRedPoint(pointList);
        // 新夺宝奇兵小红点
        sendNewTreasureRaiderRedPoint(pointList);
        sendNewTreasureRaider3RedPoint(pointList);
        sendNewTreasureRaider4RedPoint(pointList);
        sendNewTreasureRaider4RedPoint(pointList);
        sendTreasureRaider139RedPoint(pointList);
        // 王的后宫小红点
        sendHaremRedPoint(pointList);
        // 捞金鱼
        sendGoldFishRedPoint(pointList);
        // 神装锻造红点
        sendForgingRedPoint(pointList);
        // 仙女的保佑红点
        sendFairyBlessRedPoint(pointList);
        // 少女的邂逅活动红点
        sendMaidenEncounterRedPoint(pointList);
        // 限定特典(限时限购)活动红点
        sendTimeLimitPurchaseRedPoint(pointList);
        // 咏花吟月(充值折扣礼包)红点
        sendDiscountGiftRedPoint(pointList);
        // 鬼节活动红点
        sendObonRedPoint(pointList);
        // 天降元宝活动红点
        sendWelfareRewardRedPoint(pointList);
        // 新手UR活动红点
        sendNewURRedPoint(pointList);
        // 大转盘活动红点
        sendTurntableRedPoint(pointList);
        // 万圣节活动红点
        sendHalloweenRedPoint(pointList);
        // 复刻版神将投放小红点
        sendReleaseURRedPoint(pointList);
        // 7日之诗投放小红点
        sendSevenLoginRedPoint(pointList);
        // 30日签到
        sendLoginSignedRedPoint(pointList);
        // 新手扭蛋小红点
        sendNewDanRedPoint(pointList);
        // 周卡活动奖励
        sendWeekCardRedPoint(pointList);
        // 新夺宝奇兵复刻版小红点
        sendNewTreasureRaider2RedPoint(pointList);
        // 累计消耗钻石购买育成丹
        sendConsumeItemRedPoint(pointList);
        // UR抽卡复刻版小红点
        sendRelease2URRedPoint(pointList);

        sendActivity121RedPoint(pointList);
        // 活跃度达标小红点提示
        sendActiveComplianceRedPoint(pointList);

        sendContinueRechargeMoneyRedPoint(pointList);
        // 束缚彼女活动小红点
        sendActivity123URRedPoint(pointList);

        // 充值返利抽奖活动
        sendActivity124URRedPoint(pointList);
        // 武器召唤师
        sendActivity127URRedPoint(pointList);

        // 武器屋
        sendActivity125WeaponRedPoint(pointList);

        // 新用户许愿池红点
        sendWelfareRewardByRegDateRedPoint(pointList);
        // 消耗型周卡
        sendConWeekCardRedPoint(pointList);
        // 消耗型月卡
        sendConsumeMonthCardRedPoint(pointList);

        // 抽卡排行活动
        sendActivity128UrRankRedPoint(pointList);

        // 等级礼包
        sendActivity132LevelGiftRedPoint(pointList);
        
        //關卡禮包
        sendActivity151StageGiftRedPoint(pointList);

        // 周末礼包
        sendActivity134WeekendGiftRedPoint(pointList);

        // 老虎机充值返利活动
        sendActivity137RedPoint(pointList);
        // 俄罗斯轮盘活动 
        sendActivity140RedPoint(pointList);

        if (pointList.size() == 0)
            return;
        HPRedPointInfo.Builder builder = HPRedPointInfo.newBuilder();
        builder.addAllPointActivityIdList(pointList);

        player.sendProtocol(Protocol.valueOf(HP.code.RED_POINT_LIST_SYNC_S_VALUE, builder));

    }

    /***
     * 给客户端推送指定活动的小红点
     *
     * @param activityIDs
     */
    public void pushClientShowRedPointByID(int... activityIDs) {
        if (activityIDs.length == 0) {
            // 推送全部活动的小红点
            // pushClientShowRedPoint(); //(若有需要，则解开该行代码的注释)
            return;
        }
        List<Integer> pointList = new ArrayList<Integer>();
        for (Integer activityID : activityIDs) {
            switch (activityID) {
                case Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE:
                    sendAccConsumeAwardRedPoint(pointList);
                    break;
                case Const.ActivityId.VIP_WELFARE_VALUE:
                    sendVipWelfareRedPoint(pointList);
                    break;
                case Const.ActivityId.FIRST_GIFTPACK_VALUE:
                    sendFirstGiftRedPoint(pointList);
                    break;
                case Const.ActivityId.MONTH_CARD_VALUE:
                    sendMonthCardRedPoint(pointList);
                    break;
                case Const.ActivityId.VIP_PACKAGE_VALUE:
                    sendVipPackageRedPoint(pointList);
                    break;
                case Const.ActivityId.SHOOT_ACTIVITY_VALUE:
                    sendShootRedPoint(pointList);
                    break;
                case Const.ActivityId.SALE_PACKET_VALUE:
                    sendSalePacketRedPoint(pointList);
                    break;
                case Const.ActivityId.CONTINUE_RECHARGE_VALUE:
                    sendRechargeRebateRedPoint(pointList);
                    break;
                case Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE:
                    sendAccRechargePoint(pointList);
                    break;
                case Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE:
                    sendAccLoginPoint(pointList);
                    break;
                case Const.ActivityId.SINGLE_RECHARGE_VALUE:
                    sendSingleRechargePoint(pointList);
                    break;
                case Const.ActivityId.PRINCE_DEVILS_VALUE:
                    sendPrinceDevilsPoint(pointList);
                    break;
                case Const.ActivityId.TREASURE_RAIDER_VALUE:
                    sendTreasurePoint(pointList);
                    break;
                case Const.ActivityId.GROWTH_FUND_VALUE:
                    sendGrowthFundPoint(pointList);
                    break;
                case Const.ActivityId.NEW_TREASURE_RAIDER_VALUE:
                    sendNewTreasureRaiderRedPoint(pointList);
                    break;
                case Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE:
                    sendNewTreasureRaider3RedPoint(pointList);
                    break;
                case Const.ActivityId.NEW_TREASURE_RAIDER4_VALUE:
                    sendNewTreasureRaider4RedPoint(pointList);
                    break;
                case Const.ActivityId.GOLD_FISH_VALUE:
                    sendGoldFishRedPoint(pointList);
                    break;
                case Const.ActivityId.GODEQUIP_FORGING_VALUE:
                    sendForgingRedPoint(pointList);
                    break;
                case Const.ActivityId.NEW_UR_VALUE:
                    sendNewURRedPoint(pointList);
                    break;
                case Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE:
                    sendSevenLoginRedPoint(pointList);
                    break;
                case Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE:
                    sendNewTreasureRaider2RedPoint(pointList);
                    break;
                case Const.ActivityId.RELEASE_UR_VALUE:
                    sendReleaseURRedPoint(pointList);
                    break;
                case Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE:
                    sendConsumeItemRedPoint(pointList);
                    break;
                case Const.ActivityId.RELEASE_UR2_VALUE:
                    // UR抽卡复刻版小红点
                    sendRelease2URRedPoint(pointList);
                    break;
                case Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE:
                    sendContinueRechargeMoneyRedPoint(pointList);
                    break;
                case Const.ActivityId.RELEASE_UR3_VALUE:
                    sendActivity121RedPoint(pointList);
                    break;
                case Const.ActivityId.ACTIVECOMPLIANCE_VALUE:
                    sendActiveComplianceRedPoint(pointList);
                    break;
                case Const.ActivityId.ACTIVITY123_UR_VALUE:
                    sendActivity123URRedPoint(pointList);
                    break;
                case Const.ActivityId.ACTIVITY127_UR_VALUE:
                    sendActivity127URRedPoint(pointList);
                    break;
                case Const.ActivityId.CONSUME_MONTH_CARD_VALUE:
                    sendConsumeMonthCardRedPoint(pointList);
                    break;
                case Const.ActivityId.CONSUME_WEEK_CARD_VALUE:
                    sendConWeekCardRedPoint(pointList);
                    break;
                case Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE:
                    sendActivity132LevelGiftRedPoint(pointList);
                    break;
                case Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE:
                	sendTreasureRaider139RedPoint(pointList);
                    break;
                case Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE:
                	sendActivity151StageGiftRedPoint(pointList);
                	break;
            }
        }
        if (pointList.size() == 0) {
            // 去掉不必要的网络IO操作
            return;
        }
        HPRedPointInfo.Builder builder = HPRedPointInfo.newBuilder();
        builder.addAllPointActivityIdList(pointList);
        player.sendProtocol(Protocol.valueOf(HP.code.RED_POINT_SINGLE_SYNC_S_VALUE, builder));
    }

    /**
     * 发送累计消费小红点
     */
    public void sendAccConsumeAwardRedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        Map<Object, AccConsumeCfg> cfgMap = ConfigManager.getInstance().getConfigMap(AccConsumeCfg.class);
        if (cfgMap == null || cfgMap.size() == 0) {
            return;
        }
        Collection<AccConsumeCfg> values = cfgMap.values();
        AccConsumeStatus accConsumeStatues = null;
        int nowMonth = GuaJiTime.getCalendar().get(Calendar.MONTH)+1;
        for (AccConsumeCfg cfg : values) {
            accConsumeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(),
                    AccConsumeStatus.class);
            if (accConsumeStatues == null) {
                continue;
            }
            if ((nowMonth % 2) != 0) { // 奇數月
            	if  (cfg.getId() > ConfigManager.getInstance().getConfigMap(AccConsumeCfg.class).size()/2) {
            		continue;
            	}
            	if (accConsumeStatues.getAccConsumeGold() >= cfg.getSum() && !accConsumeStatues.isAlreadyGot(cfg.getId())) {
            		pointList.add(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE);
            		break;
            	}
            }
            else {
            	if  (cfg.getId() <= ConfigManager.getInstance().getConfigMap(AccConsumeCfg.class).size()/2) {
            		continue;
            	}
            	if (accConsumeStatues.getAccConsumeGold() >= cfg.getSum() && !accConsumeStatues.isAlreadyGot(cfg.getId())) {
            		pointList.add(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE);
            		break;
            	}
            }
        }
    }

    /**
     * 坚持新夺宝奇兵活动的小红点
     */
    public void checkNewTreasureRaiderRP() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg timesCfg = NewTreasureRaiderTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_TREASURE_RAIDER_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    public void checkNewTreasureRaider2RP() {
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg2 timesCfg = NewTreasureRaiderTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus2.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler2.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    /**
     * 坚持新夺宝奇兵活动的小红点
     */
    public void checkNewTreasureRaiderRP3() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg3 timesCfg = NewTreasureRaiderTimesCfg3.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus3 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus3.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler3.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    public void checkNewTreasureRaiderRP4() {
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER4_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg4 timesCfg = NewTreasureRaiderTimesCfg4.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus4 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus4.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler2.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_TREASURE_RAIDER4_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    /**
     * 发送新夺宝奇兵小红点
     */
    public void sendNewTreasureRaiderRedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg timesCfg = NewTreasureRaiderTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 发送新夺宝奇兵复刻版小红点
     */
    public void sendNewTreasureRaider2RedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }

        NewTreasureRaiderTimesCfg2 timesCfg = NewTreasureRaiderTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }

        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }

        NewTreasureRaiderStatus2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus2.class);

        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler2.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }

    }

    /**
     * 发送新夺宝奇兵小红点
     */
    public void sendNewTreasureRaider3RedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        NewTreasureRaiderTimesCfg3 timesCfg = NewTreasureRaiderTimesCfg3.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus3 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus3.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler3.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    public void sendNewTreasureRaider4RedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER4_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }

        NewTreasureRaiderTimesCfg4 timesCfg = NewTreasureRaiderTimesCfg4.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }

        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }

        NewTreasureRaiderStatus4 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus4.class);

        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler4.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }

    }

    public static void pushGrowthFundPoint(Player player) {
        if (player != null) {
            // 有时间改成多态
            List<Integer> pointList = new ArrayList<Integer>();

            GrowthFundStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                    Const.ActivityId.GROWTH_FUND_VALUE, 0, GrowthFundStatus.class);
            if (status == null) {
                return;
            }

            if (status.showRedPoint(player.getVipLevel(), player.getLevel())) {
                // 红点
                pointList.add(Const.ActivityId.GROWTH_FUND_VALUE);
            }

            HPRedPointInfo.Builder builder = HPRedPointInfo.newBuilder();
            builder.addAllPointActivityIdList(pointList);

            player.sendProtocol(Protocol.valueOf(HP.code.RED_POINT_LIST_SYNC_S_VALUE, builder));
        }

    }

    /**
     * 首充礼包提示红点
     */
    public void sendFirstGiftPackStatus() {
        boolean isFirstRecharge = player.getPlayerData().getPlayerEntity().getRecharge() == 0 ? true : false;
        FirstGiftPackStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.FIRST_GIFTPACK_VALUE, 0, FirstGiftPackStatus.class);
        if (status.getLastAwareTime() == null) {
            status.setGiftStatus(FirstGiftPackAwardHandler.NOTGET);
            if (isFirstRecharge == true) {// 没有过充值
                status.setIsFirstPay(FirstGiftPackAwardHandler.NOFIRSTPAY);
            } else {
                status.setIsFirstPay(FirstGiftPackAwardHandler.FIRSTPAY);
            }
        } else {
            status.setGiftStatus(FirstGiftPackAwardHandler.GOTED);
        }
        // 获取首充礼包的实体信息并设置新的领取状态
        HPFirstRechargeGiftInfo.Builder builder = HPFirstRechargeGiftInfo.newBuilder();
        builder.setGiftStatus(status.getGiftStatus());
        builder.setIsFirstPay(status.getIsFirstPay());
        // 发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.FIRST_GIFTPACK_INFO_S, builder));
    }

    /**
     * 请求ios专属礼包状态
     *
     * @param hawkProtocol
     * @return
     */
    @ProtocolHandlerAnno(code = HP.code.IOS_GIT_C_VALUE)
    private boolean onIosExclusiveGit(Protocol hawkProtocol) {
        StateEntity stateEntity = player.getPlayerData().getStateEntity();
        if (stateEntity == null) {
            player.sendError(hawkProtocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
            return false;
        }
        HPIosGitInfo.Builder builder = HPIosGitInfo.newBuilder();
        builder.setGitState(stateEntity.getIosGetState());
        // 发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.IOS_GIT_S_VALUE, builder));
        return true;
    }

    /**
     * 领取ios专属礼包
     *
     * @param hawkProtocol
     * @return
     */
    @ProtocolHandlerAnno(code = HP.code.IOS_GIT_GET_C_VALUE)
    private boolean getIosExclusiveGit(Protocol hawkProtocol) {
        StateEntity stateEntity = player.getPlayerData().getStateEntity();
        if (stateEntity == null) {
            player.sendError(hawkProtocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
            return true;
        }

        if (stateEntity.getIosGetState() == Const.IosGitState.HAVE_GET_VALUE) {
            player.sendError(hawkProtocol.getType(), Status.error.TODAY_FREE_RED_ENVELOPE_GOT);
            return true;
        }
        AwardItems awardItems = AwardItems.valueOf(SysBasicCfg.getInstance().getIos_git_reward());
        awardItems.rewardTakeAffectAndPush(player, Action.IOS_GIT, 2);
        stateEntity.setIosGetState(Const.IosGitState.HAVE_GET_VALUE);
        stateEntity.notifyUpdate();

        HPIosGitInfo.Builder builder = HPIosGitInfo.newBuilder();
        builder.setGitState(stateEntity.getIosGetState());

        player.sendProtocol(Protocol.valueOf(HP.code.IOS_GIT_GET_S_VALUE, builder));
        return true;
    }

    /**
     * 发送王的后宫（百花美人）小红点
     */
    private void sendHaremRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.HAREM_VALUE;
        int[] haremTypes = new int[]{Const.HaremType.HAREM_TYPE_COMMON_VALUE,
                Const.HaremType.HAREM_TYPE_ADVANCED_VALUE, Const.HaremType.HAREM_TYPE_MIDDLE_VALUE};
        for (int haremType : haremTypes) {
            HPHaremInfo.Builder builder = HaremManager.getHaremInfo(player, haremType);

            // 可以免费
            if (builder.getFreeChance() > 0 && builder.getFreeCd() <= 0) {
                pointList.add(activityId);
                return;
            }
        }
    }

    /**
     * 神装锻造活动红点
     */
    private void sendForgingRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.GODEQUIP_FORGING_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ForgingStatus forgingStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
                Const.ActivityId.GODEQUIP_FORGING_VALUE, timeCfg.getStageId(), ForgingStatus.class);
        if (forgingStatus == null) {
            return;
        }
        if (forgingStatus.isCanFree()) {
            pointList.add(activityId);
        }
    }

    /**
     * 仙女的保佑活动红点
     */
    private void sendFairyBlessRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.FAIRY_BLESS_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (null == timeCfg)
            return;
        FairyBlessStatus fairyBlessStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), FairyBlessStatus.class);
        if (null == fairyBlessStatus)
            return;
        // 每天拥有的花数大于最低档的时候显示小红点 只要进入界面则小红点消失
        int flowerCount = fairyBlessStatus.getFlowerCount();
        if (flowerCount >= FairyBlessCfg.lowestLevelFlower) {
            pointList.add(activityId);
        }
    }

    /**
     * 少女的邂逅活动红点
     */
    private void sendMaidenEncounterRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.MAIDEN_ENCOUNTER_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
        if (null == timeCfg)
            return;
        MaidenEncounterEntity entity = player.getPlayerData().getMaidenEncounterEntity();
        if (null == entity)
            return;
        // 有免费互动次数显示红点
        if (entity.getSurplusFreeInteractTimes() > 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 限定特典(限时限购)活动红点
     */
    private void sendTimeLimitPurchaseRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (null == timeCfg)
            return;
        PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
                activityId, timeCfg.getStageId(), PersonalTimeLimitStatus.class);
        if (null == personalTimeLimitStatus)
            return;
        if (personalTimeLimitStatus.shouldShowPoint()) {
            pointList.add(activityId);
        }
    }

    /**
     * 咏花吟月(充值折扣礼包)红点
     */
    private void sendDiscountGiftRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.DISCOUNT_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (null == timeCfg)
            return;
        @SuppressWarnings("unchecked")
        ActivityEntity<DiscountGiftData> activityEntity = (ActivityEntity<DiscountGiftData>) player.getPlayerData()
                .getActivityEntity(Const.ActivityId.DISCOUNT_GIFT_VALUE, timeCfg.getStageId());
        if (null == activityEntity)
            return;
        DiscountGiftData data = activityEntity.getActivityStatus(DiscountGiftData.class);
        if (null == data)
            return;
        if (data.shouldShowPoint()) {
            pointList.add(activityId);
        }
    }

    /**
     * 发送鬼节活动小红点 1、有免费次数的时候显示小红点 2、当阶段完成有宝箱未领取时显示小红点
     */
    private void sendObonRedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.OBON_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ObonTimesCfg timesCfg = ObonTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        ObonStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(),
                ObonStatus.class);
        int lastFreeTime = status.getLastFreeTime();
        int currentTime = GuaJiTime.getSeconds();
        // 有免费次数
        int freeCD = Math.max(ObonManager.hourToSec(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
            return;
        }
        // 阶段完成但是礼包没有领取
        Set<Integer> stages = ObonStageCfg.stageMap.keySet();
        for (Integer stage : stages) {
            if (null != ObonManager.getGift(status, stage)) {
                pointList.add(activityId);
                return;
            }
        }
    }

    /**
     * 发送天降元宝活动红点
     */
    private void sendWelfareRewardRedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.WELFARE_REWARD_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            return;
        }
        WelfareRewardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), WelfareRewardStatus.class);
        if (null == status)
            return;
        WelfareRewardCfg cfg = WelfareRewardManager.getRandomCfg(status);
        // 钻石够消耗并且可以操作推送红点
        if (null != cfg) {
            if (player.getGold() >= cfg.getCost() && status.canPlay()) {
                pointList.add(activityId);
            }
        }
    }

    /**
     * 检测新手UR活动的小红点
     */
    public void checkNewURRedPoint() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_UR_VALUE;
        NewURStatus status = ActivityUtil.getNewURStatus(player.getPlayerData());
        if (!NewURManager.canPlay(player.getLevel(), status))
            return;
        NewURTimesCfg timesCfg = NewURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null || timesCfg.getFreeCountDown() == 0) {
            return;
        }
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewURManager.toMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_UR_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    /**
     * 发送新手UR活动小红点
     */
    public void sendNewURRedPoint(List<Integer> pointList) {
        NewURStatus status = ActivityUtil.getNewURStatus(player.getPlayerData());
        if (!NewURManager.canPlay(player.getLevel(), status))
            return;
        NewURTimesCfg timesCfg = NewURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewURManager.toMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(Const.ActivityId.NEW_UR_VALUE);
        }
    }

    /**
     * 发送大转盘活动小红点
     */
    public void sendTurntableRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.TURNTABLE_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
        if (timeCfg == null)
            return;
        TurntableStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), TurntableStatus.class);
        if (status == null) {
            return;
        }
        TurntableConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(TurntableConstCfg.class, 1);
        if (null == constCfg) {
            player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.DATA_NOT_FOUND);
            return;
        }
        if (TurntableManager.canFreeDraw(status, constCfg)) {
            pointList.add(activityId);
        }
    }

    /**
     * 发送万圣节活动小红点
     */
    public void sendHalloweenRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.HALLOWEEN_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
        if (timeCfg == null)
            return;
        HalloweenStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), HalloweenStatus.class);
        if (status == null) {
            return;
        }
        HalloweenConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(HalloweenConstCfg.class, 1);
        if (null == constCfg) {
            player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.DATA_NOT_FOUND);
            return;
        }
        if (HalloweenManager.canFreeDraw(status, constCfg)) {
            pointList.add(activityId);
        }
    }

    /**
     * 检测复刻版神将投放的小红点
     */
    public void checkReleaseURRedPoint() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.RELEASE_UR_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg timesCfg = ReleaseURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        ReleaseURStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), ReleaseURStatus.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = ReleaseURInfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.RELEASE_UR_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    /**
     * 发送版神将投放小红点
     */
    public void sendReleaseURRedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.RELEASE_UR_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg timesCfg = ReleaseURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        ReleaseURStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), ReleaseURStatus.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = ReleaseURInfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 发送复刻版神将投放小红点
     */
    public void sendRelease2URRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.RELEASE_UR2_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg2 timesCfg = ReleaseURTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        ReleaseURStatu2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), ReleaseURStatu2.class);
        if (status == null)
            return;

        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = ReleaseURInfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }
    


    /**
     * 发送束缚彼女小红点
     */
    public void sendActivity123URRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACTIVITY123_UR_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg123 timesCfg = ReleaseURTimesCfg123.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() <= 0)
            return;
        Activity123Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity123Status.class);
        if (status == null)
            return;

        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = Activity123InfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 武器召唤师
     */
    public void sendActivity127URRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACTIVITY127_UR_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ActivityURTimes127 timesCfg = ActivityURTimes127.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        Activity127Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity127Status.class);
        if (status == null)
            return;

        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = Activity127InfoHandler.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 充值返利抽奖活动
     */
    public void sendActivity124URRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACTIVITY124_RECHARGE_RETURN_VALUE;
        // 检测活动是否开放
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            return;
        }
        // 剩余时间
        int leftTime = activityTimeCfg.calcActivitySurplusTime();
        if (leftTime <= 0) {
            // 活动已关闭
            return;
        }
        Activity124Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                activityTimeCfg.getStageId(), Activity124Status.class);
        if (status == null) {
            return;
        }
        if (!status.getGotTicket()) {
            pointList.add(activityId);
        }
    }

    /**
     * 老虎机充值返利抽奖活动
     */
    public void sendActivity137RedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
        // 检测活动是否开放
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            return;
        }

        // 活动是否开启
        Activity137Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                activityTimeCfg.getStageId(), Activity137Status.class);
        long thisEndTime = status.getActivityTime() + SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
        long currentTime = System.currentTimeMillis();
        long activityEndTime = activityTimeCfg.getlEndTime();
        // 如果活动结束未过期延长活动时间
        if (thisEndTime > activityEndTime) {
            activityEndTime = thisEndTime;
        }
        if (activityEndTime < currentTime) {
            // 活动已关闭
            return;
        }

        // 在有效期内使用
        boolean unUsed = thisEndTime > currentTime && !status.isUsed();

        if (unUsed && status.getLotteryCount() == 0) {
            pointList.add(activityId);
        }
    }
    
    /**
     * 俄罗斯轮盘
     */
    public void sendActivity140RedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.RUSSIADISHWHEEL_VALUE;
        // 检测活动是否开放
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            return;
        }

        // 活动是否开启
        Activity140Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                activityTimeCfg.getStageId(), Activity140Status.class);
        
    	List<String> takeConditions = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));
    	
    	if(!takeConditions.contains(String.valueOf(status.getLoginTimes()))) {
    		return;
    	}
        long thisEndTime = status.getActivityTime() + SysBasicCfg.getInstance().getActivity140Continuetime() * 1000 * 60;
        long currentTime = System.currentTimeMillis();
        long activityEndTime = activityTimeCfg.getlEndTime();
        // 如果活动结束未过期延长活动时间
        if (thisEndTime > activityEndTime) {
            activityEndTime = thisEndTime;
        }
        if (activityEndTime < currentTime) {
            // 活动已关闭
            return;
        }

        // 在有效期内使用
        boolean unUsed = thisEndTime > currentTime && !status.isUsed();

        if (unUsed && status.getLotteryCount() == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 武器屋红点
     */
    public void sendActivity125WeaponRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACTIVITY125_WEAPON_VALUE;
        int defaultStageId = 1;
        Activity125Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, defaultStageId,
                Activity125Status.class);
        if (status == null) {
            return;
        }
        if (status.getMediumNextFreeTime() <= System.currentTimeMillis()
                || status.getHighNextFreeTime() <= System.currentTimeMillis()) {
            pointList.add(activityId);
        }
    }

    /**
     * 新用户许愿池红点
     */
    public void sendWelfareRewardByRegDateRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.WELFAREBYREGDATE_REWARD_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg
                .getActivityItem(Const.ActivityId.WELFAREBYREGDATE_REWARD_VALUE);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        WelfareRewardStatusByRegDate status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), WelfareRewardStatusByRegDate.class);
        if (status == null) {
            return;
        }
        if (!status.canPlay()) {
            return;
        }
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        Integer keepDays = activityItem.getParam("keepDays");
        if (status.isCanPlayByDate(registerDate.getTime(), timeCfg.getlStartTime(), keepDays, player.getPlayerData().getPlayerEntity().getMergeTime())) {
            pointList.add(activityId);
        } else {
            player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
        }
    }

    /**
     * 抽卡排行活动
     */
    public void sendActivity128UrRankRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.ACTIVITY128_UR_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity128Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity128Status.class);
        if (status == null) {
            return;
        }
        if (status.getNextFreeTime() <= System.currentTimeMillis()) {
            pointList.add(activityId);
        }
    }

    public void sendActivity132LevelGiftRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity132Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity132Status.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint(player.getLevel())) {
            pointList.add(activityId);
        }
    }

    public void sendActivity151StageGiftRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity151Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity151Status.class);
        if (status == null) {
            return;
        }
        if (status.showRedPoint(player.getPassMapId())) {
            pointList.add(activityId);
        }
    }
    
    public void sendActivity134WeekendGiftRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.ACTIVITY134_WEEKEND_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity134Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), Activity134Status.class);
        if (status == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        boolean inActivityDay = currentTime > status.getStartTime() && currentTime < status.getRestTime();
        if (inActivityDay) {
            int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            Activity134StatusItem item = status.getStatusMap().get(dayOfWeek);
            boolean hasFreeLottery = item != null && item.getCount() == 0;
            if (hasFreeLottery) {
                pointList.add(activityId);
            }
        }
    }

    /**
     * 检测121 活动小红点的投放
     */
    public void checkActivty121RRedPoint() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.RELEASE_UR3_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg121 timesCfg = ReleaseURTimesCfg121.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        ReleaseURStatus121 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), ReleaseURStatus121.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = ReleaseURInfoHandler121.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.RELEASE_UR3_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }

    /**
     * 121活动投放小红点
     */
    public void sendActivity121RedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.RELEASE_UR3_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ReleaseURTimesCfg121 timesCfg = ReleaseURTimesCfg121.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null)
            return;
        if (timesCfg.getFreeCountDown() == 0)
            return;
        ReleaseURStatus121 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), ReleaseURStatus121.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = ReleaseURInfoHandler121.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }

    /**
     * 活跃达标小红点提示
     *
     * @param pointList
     */

    public void sendActiveComplianceRedPoint(List<Integer> pointList) {
        // 检测活动是否开放

        int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null)
            return;
        ActiveStatus status = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
        if (status == null)
            return;
        
        if (status.calcActivitySurplusTime() <= 0)
        	return;
                
        if (status.getDays() > status.getAwardDays().size()) {
            pointList.add(activityId);
        }

    }

    /**
     * 121活动投放小红点
     */
    public void sendActiveComplianceRRedPoint(List<Integer> pointList) {

    }

    /**
     * 发送7日之诗小红点
     */
    public void sendSevenLoginRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE;
        int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
                

        if (surplusTime <= 0) {
            return;
        }

        // 距注册天数
        int registerDays = GuaJiTime.calcBetweenDays( player.getPlayerData().getStateEntity().getNewbieDate(),
                GuaJiTime.getCalendar().getTime()) + 1;

        SevenDayQuestEntity questEntity = player.getPlayerData().loadSevenDayQuestEntity();

        if (questEntity != null) {
            Map<Integer, SevenDayQuestItem> map = questEntity.getQuestMap();
            // 任务列表
            for (Map.Entry<Integer, SevenDayQuestItem> entry : map.entrySet()) {

                SevenDayQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class,
                        entry.getValue().getId());
                if (questCfg != null) {

                    if (entry.getValue().getStatus() == QuestState.FINISHED_VALUE) {
                        if (registerDays >= questCfg.getDays()) {
                            pointList.add(activityId);
                            return;
                        }

                    }

                }

            }

            // 活跃度领取奖励
            Map<Object, SevenDayQuestPointCfg> sevenDayPointMapCfg = ConfigManager.getInstance()
                    .getConfigMap(SevenDayQuestPointCfg.class);
            for (SevenDayQuestPointCfg pointCfg : sevenDayPointMapCfg.values()) {
                if (questEntity.getPoint() >= pointCfg.getPointNumber()) {
                    if (questEntity.getPointState().get(pointCfg.getPointNumber()) != null
                            && questEntity.getPointState().get(pointCfg.getPointNumber()) == 1) {
                        pointList.add(activityId);
                        return;
                    }
                }
            }
        }

    }

    public void sendLoginSignedRedPoint(List<Integer> pointList) {

        int activityId = Const.ActivityId.ACCUMULATIVE_LOGIN_SIGNED_VALUE;

        ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeConfig == null || player == null) {
            // 活动已关闭
            return;
        }
        // 提取活动数据
        AccLoginSignedStatus status = ActivityUtil.getAccLoginSignedStatus(player.getPlayerData());
        if (status == null) {

            return;
        }

        // 已补签次数
        int supplSignedTimes = status.getSupplSignedDays().size();
        if (GuaJiTime.getCalendar().get(Calendar.MONTH) != status.getCurMonth()) {
        	supplSignedTimes = 0;
        }
        // 获取vip 可补签次数
        VipPrivilegeCfg vipData = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class,
                player.getVipLevel());
        int canSupplSignedTimes = 0;
        if (vipData != null) {
            canSupplSignedTimes = vipData.getComplementsign();
        }
        
        int SignedTime = status.getSignedDays().size();
        if (GuaJiTime.getCalendar().get(Calendar.MONTH) != status.getCurMonth()) {
        	SignedTime = 0;
        }
        // 总签到次数
        int totalSignedTimes = supplSignedTimes + SignedTime;

        // 活跃度领取奖励
        Map<Object, accLoginSignedPointCfg> signedPointMapCfg = ConfigManager.getInstance()
                .getConfigMap(accLoginSignedPointCfg.class);

        for (accLoginSignedPointCfg pointCfg : signedPointMapCfg.values()) {

            if (totalSignedTimes >= pointCfg.getPointNumber()
                    && !status.getGotAwardChest().contains(pointCfg.getPointNumber())) {
                pointList.add(activityId);
                return;
            }

        }

        // 补签次数和签到次数大于等于当前天数
        if ((SignedTime + supplSignedTimes) >= GuaJiTime.getMonthDay())
            return;

        // 有补签次数
        if (supplSignedTimes < canSupplSignedTimes) {
            pointList.add(activityId);
            return;
        }
        // 今天是否签到
        if (!status.getSignedDays().contains(GuaJiTime.getMonthDay())||(GuaJiTime.getCalendar().get(Calendar.MONTH) != status.getCurMonth())) {
            pointList.add(activityId);
            return;
        }

    }

    /**
     * 发送新手扭蛋小红点
     *
     * @param pointList
     */
    public void sendNewDanRedPoint(List<Integer> pointList) {
        HaremActivityEntity entity = player.getPlayerData().getHaremActivityEntity();
        if (HaremManager.isNewStrictOpen(entity)) {
            HaremConstCfg haremCfg = ConfigManager.getInstance().getConfigByKey(HaremConstCfg.class,
                    Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE);
            if (haremCfg != null) {

                int lastFreeTime = entity.getLastNewStrictFreeTime();
                int freeChance = entity.getNewStrictFreeChance();
                if (lastFreeTime != 0) {
                    lastFreeTime = Math.max(haremCfg.getFreeCd() - (GuaJiTime.getSeconds() - lastFreeTime), 0);
                }
                if (lastFreeTime == 0 && freeChance >= 1) {
                    pointList.add(Const.ActivityId.NEW_ND_VALUE);
                }

            }

        }
    }

    /**
     * @param pointList
     */
    public void sendConsumeItemRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE;

        // 检测活动是否开放
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }

        AccConItemStatus accConsumeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), AccConItemStatus.class);
        if (accConsumeStatues == null)
            return;

        Map<Integer, ConsumeItem> map = accConsumeStatues.getConsumeItems();
        for (ConsumeItem item : map.values()) {
            AccConsumeItemCfg itemCfg = AccConsumeItemCfg.getConsumeItemById(item.getGoodId());
            if (itemCfg == null || itemCfg.getNeedTimes() == 0) {
                continue;
            }
            if ((item.getBuytime() / itemCfg.getNeedTimes()) > item.getPrizeTime() / itemCfg.getNeedTimes()) {
                pointList.add(activityId);
                return;
            }

        }

    }

    /**
     * @param pointList 周卡活动小红点
     */
    public void sendWeekCardRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.WEEK_CARD_VALUE;

        ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeConfig == null || player == null) {
            // 活动已关闭
            return;
        }

        WeekCardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeConfig.getStageId(), WeekCardStatus.class);
        if (status == null) {

            return;
        }

        if (status.getLeftDays() > 0 && !status.isRewardToday()) {
            pointList.add(Const.ActivityId.WEEK_CARD_VALUE);
        }
    }

    /**
     * @param pointList 消耗型周卡活动小红点
     */
    public void sendConWeekCardRedPoint(List<Integer> pointList) {
        int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;

        ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeConfig == null || player == null) {
            // 活动已关闭
            return;
        }

        ConWeekCardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeConfig.getStageId(), ConWeekCardStatus.class);
        if (status == null) {

            return;
        }

        if (status.getLeftDays() > 0 && !status.isRewardToday()) {
            pointList.add(Const.ActivityId.CONSUME_WEEK_CARD_VALUE);
        }
    }
    

    /**
     * 坚持新夺宝奇兵活动的小红点
     */
    public void checkNewTreasureRaider139RP() {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        Activity139TimesCfg timesCfg = Activity139TimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus139 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus139.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler139.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            Long lastSendTime = lastSendRedPointTimes.get(activityId);
            if (lastSendTime == null || lastSendTime < (currentTime - duration)) {
                pushClientShowRedPointByID(Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE);
                lastSendRedPointTimes.put(activityId, currentTime);
            }
        }
    }
    
    /**
     * 发送新夺宝奇兵小红点
     */
    public void sendTreasureRaider139RedPoint(List<Integer> pointList) {
        // 检测活动是否开放
        int activityId = Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        Activity139TimesCfg timesCfg = Activity139TimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
        if (timesCfg == null) {
            return;
        }
        if (timesCfg.getFreeCountDown() == 0) {
            return;
        }
        NewTreasureRaiderStatus139 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
                timeCfg.getStageId(), NewTreasureRaiderStatus139.class);
        long lastFreeTime = status.getLastFreeTime();
        long currentTime = System.currentTimeMillis();
        long duration = NewTreasureRaiderInfoHandler139.convertTimeToMillisecond(timesCfg.getFreeCountDown());
        int freeCD = (int) Math.max(duration - (currentTime - lastFreeTime), 0);
        if (freeCD == 0) {
            pointList.add(activityId);
        }
    }
    
	/**
	 * 壁尻任務計數數量達成
	 * @param msg
	 * @return
	 */
	private boolean onGloryHoleDailyCount(Msg msg) {
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY175_Glory_Hole_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return false;
		}
		// 剩余时间
//		int leftTime = activityTimeCfg.calcActivitySurplusTime();
//		if (leftTime <= 0) {
//			// 活动已关闭
//			return false;
//		}
		
		Activity175Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity175Status.class);
		
		if (status == null) {
			return false;
		}
		
		int count = (Integer) msg.getParams().get(0);

	
		Map<Integer, DailyQuestItem> map = status.getDailyQuestMap();

		for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
			int id = entry.getValue().getId();
			GloryHoleDailyCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(GloryHoleDailyCfg.class, id);

			if (dailyQuestCfg == null) {
				continue;
			}
			
			int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;

			if (dailyQuestCfg.getType() == QuestType)
			{
				entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);

				if (entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg()) {
					entry.getValue().setQuestStatus(1);
				}
			}

		}

		player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

		//sendAllDailyQuestInfo(HP.code.DAILY_QUEST_INFO_S_VALUE);

		return true;
	}
	
	/**
	 * 循環關卡計數數量達成
	 * @param msg
	 * @return
	 */
	private boolean onCycleStageDailyCount(Msg msg) {
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			//191沒開,清除191活動資訊
			ActivityUtil.CycleStageClearItem(player);
			
			activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
			
			activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			
			if (activityTimeCfg == null) {
				ActivityUtil.CycleStageClearItem2(player);
				return false;
			}
			
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
					Activity196Status.class);
			
			if (status == null) {
				return false;
			}
			
			int count = (Integer) msg.getParams().get(0);
	
		
			Map<Integer, DailyQuestItem> map = status.getDailyQuestMap();
	
			for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
				int id = entry.getValue().getId();
				ActivityDailyQuest196Cfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(ActivityDailyQuest196Cfg.class, id);
	
				if (dailyQuestCfg == null) {
					continue;
				}
				
				int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;
	
				if (dailyQuestCfg.getType() == QuestType)
				{
					entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);
	
					if (entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg()) {
						entry.getValue().setQuestStatus(1);
					}
				}
	
			}
			
			player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
			
		} else {

			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
					Activity191Status.class);
			
			if (status == null) {
				return false;
			}
			
			int count = (Integer) msg.getParams().get(0);
	
		
			Map<Integer, DailyQuestItem> map = status.getDailyQuestMap();
	
			for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
				int id = entry.getValue().getId();
				ActivityDailyQuestCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(ActivityDailyQuestCfg.class, id);
	
				if (dailyQuestCfg == null) {
					continue;
				}
				
				int QuestType = msg.getMsg() % GsConst.MsgType.DailyQuestMsg.MSG_BASE;
	
				if (dailyQuestCfg.getType() == QuestType)
				{
					entry.getValue().setCompleteCount(entry.getValue().getCompleteCount() + count);
	
					if (entry.getValue().getCompleteCount() >= dailyQuestCfg.getCompleteCountCfg()) {
						entry.getValue().setQuestStatus(1);
					}
				}
	
			}
	
			player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
			
		}


		return true;
	}
}

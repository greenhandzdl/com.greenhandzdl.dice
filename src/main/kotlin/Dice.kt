package com.greenhandzdl.dice

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.isBotMuted
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Dice
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.utils.info
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random


object Dice : KotlinPlugin(
    JvmPluginDescription(
        id = "com.greenhandzdl.dice",
        name = "dice",
        version = "0.0.1",
    ) {
        author("greenhandzdl")
    }
) {
    override fun onEnable() {
        logger.info { "Dice loaded" }

        if(!File("$configFolder/admin").exists()){
            File("$configFolder/admin").createNewFile()
        }
        val admin = File("$configFolder/admin").readText()

        globalEventChannel().subscribeAlways<GroupMessageEvent> {

            val file = File("$dataFolder/${group.id.toString()}")
            if(!file.exists()){
                file.createNewFile()
            }

            if(group.isBotMuted){
                file.delete()
            }else{
                when {
                    message.contentToString() == "rd" -> {
                        val r = Random.nextInt(1, 7)
                        group.sendMessage(Dice(r))
                        file.appendText("${sender.nick}(${sender.id.toString()}) 在${LocalDate.now()} ${LocalTime.now()}扔出点数为$r \n")
                    }

                    message.contentToString() == "rd history" -> {
                        val forward: ForwardMessage = buildForwardMessage {
                            add(bot.id, bot.nick, PlainText("${file.readText()}"))
                        }
                        subject.sendMessage(forward)
                    }

                    message.contentToString() == "rd clean" -> {
                        if (sender.id.toString() != admin) {
                            group.sendMessage("你没有相应权限操作")
                        } else {
                            file.delete()
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        logger.info{"Dice disabled"}
    }
}
package com.chao.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import j2html.tags.ContainerTag;
import lombok.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.body;

public class Novel {

    public static Integer WEB_WIDTH = 750;

    public static String title = "无题";

    @Data
    public static class Sentence {
        String comment;
        String userName;
    }

    public static void deal(Long postId) {
        JSONObject comments = HttpUtil.doGetJson("https://chao.fun//api/v0/list_comments?postId=" + postId + "&order=old");
        System.out.println(comments.toJSONString());
        JSONArray jsonObjectJSONArray =   comments.getJSONArray("data");

        List<Sentence> result = new ArrayList<>();
        for (Object object : jsonObjectJSONArray) {

            JSONObject jsonObject = (JSONObject) object;

            if (jsonObject.getInteger("dep").equals(0)) {
                Sentence sentence = new Sentence();
                sentence.setComment(jsonObject.getString("text"));
                sentence.setUserName(jsonObject.getJSONObject("userInfo").getString("userName"));
                result.add(sentence);
            }
        }

        toHTML(result);

        System.out.println(JSON.toJSON(result));
        System.exit(-1);
    }


    public static void toHTML(List<Sentence> sentences) {
        try {
            File file = new File("./novel.html");

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            ContainerTag body = body(
                    meta().withCharset("utf-8")
            );

            body.withStyle("width: " + WEB_WIDTH + "px");

            body.with(div( title ).withStyle("width: 100%; text-align: center; align-items: center; font-family: \"Roboto\",sans-serif!important; font-size: 3rem!important;font-weight: 1000;"));
            for (Sentence sentence: sentences) {
                body.with(
                        div("  " + sentence.getComment())
                                .withStyle("font-family: \"Roboto\",sans-serif!important; font-size: 1.25rem!important;font-weight: 500;line-height: 2rem; opacity: .6;"))
                        .with(buildAnnotator(sentence.getUserName(), sentence.getComment()));
            }

            fileOutputStream.write(body.renderFormatted().getBytes());
            fileOutputStream.close();

        } catch (Exception ex) {

        }
    }

    public static ContainerTag buildAnnotator(String userName, String comment) {
        ContainerTag svgTag = new ContainerTag("svg")
                .withStyle("width: 100%; height: 20px");
        ContainerTag gTag= new ContainerTag("g");
        ContainerTag circleTag = new ContainerTag("circle")
                .attr("r=\"3\" fill=\"#2196F3\" cx=\"3\" cy=\"10\"");
        ContainerTag lineTag = new ContainerTag("line")
                .attr("x1=\"2\" y1=\"1\" x2=\"" + computeLength(comment) + "\" y2=\"1\" stroke=\"#2196F3\" stroke-width=\"2\" stroke-linecap=\"round\"");
        ContainerTag textTag = new ContainerTag("text")
                .attr("x=\"0\" y=\"10\" fill=\"currentColor\" dx=\"8\" dy=\"0.35em\"")
                .withStyle("    font-size: 0.75rem!important;\n" +
                "    font-weight: 250;\n" +
                "    line-height: 2rem;\n" +
                "    font-family: \"Roboto\",sans-serif!important;\n" +
                "    opacity: .6;");
        textTag.withText(userName);
        svgTag.with(gTag.with(lineTag, circleTag, textTag));
        return svgTag;
    }


    /**
     * 计算下划线的长度
     * @param comment
     * @return
     */
    public static Double computeLength(String comment) {
        Double length = 0.0;
        length = comment.length() * 20.0;
        if (length.compareTo(20.0) < 0) {
            length = 20.0;
        }
        if (length.compareTo(750.0) > 0) {
            length = 750.0;
        }
        // 2.5 是圆角
        return length -  2.5;
    }

    public static void main(String[] args) {
        Long postId = Long.valueOf(args[0]);
        if (args.length > 1) {
            title = args[1];
        }
        deal(postId);
    }
}

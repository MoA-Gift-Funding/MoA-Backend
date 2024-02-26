package moa.client.wincube.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import moa.client.wincube.dto.WincubeProductResponse.WincubeGoods.Option;

@JsonNaming(value = SnakeCaseStrategy.class)
public record WincubeProductResponse(
        String resultCode,
        String goodsNum,
        List<Map<String, Object>> goodslist
) {
    public static final String SUCCESS_CODE = "0";

    public boolean isSuccess() {
        return resultCode.equals(SUCCESS_CODE);
    }

    private static final DateTimeFormatter PERIOD_END_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public record WincubeGoods(
            String goodsId,
            String affiliate,  // 상품분류 (GS25)
            String affiliateCategory,  // 교환처분류 (편의점)
            String desc, // 상품설명
            String goodsNm, // 상품명
            String goodsImg, // 이미지 경로
            String normalSalePrice, // 소비자 가격
            LocalDate periodEnd, // 상품판매종료일 (20291231)
            int limitDate, // 유효기간 (60)
            List<Option> options
    ) {
        public record Option(
                String name, // 옵션 이름
                String code  // 옵션코드
        ) {
        }
    }

    public List<WincubeGoods> goodsList() {
        List<WincubeGoods> goodsList = new ArrayList<>();
        for (Map<String, Object> goodsMap : goodslist) {
            goodsList.add(new WincubeGoods(
                    (String) goodsMap.get("goods_id"),
                    (String) goodsMap.get("affiliate"),
                    (String) goodsMap.get("affiliate_category"),
                    (String) goodsMap.get("desc"),
                    (String) goodsMap.get("goods_nm"),
                    (String) goodsMap.get("goods_img"),
                    (String) goodsMap.get("normal_sale_price"),
                    LocalDate.parse((String) goodsMap.get("period_end"), PERIOD_END_FORMATTER),
                    Integer.parseInt((String) goodsMap.get("limit_date")),
                    getOptions(goodsMap)
            ));
        }
        return goodsList;
    }

    private List<Option> getOptions(Map<String, Object> goodsMap) {
        if (!goodsMap.containsKey("opt1_name")) {
            return Collections.emptyList();
        }

        List<Option> options = new ArrayList<>();
        int count = 1;
        do {
            options.add(new Option(
                    (String) goodsMap.get("opt" + count + "_name"),
                    (String) goodsMap.get("opt" + count + "_val")
            ));
            count++;
        } while (goodsMap.containsKey("opt" + count + "_name"));
        return options;
    }
}

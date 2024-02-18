package moa.product.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import moa.product.client.dto.WincubeProductResponse.Value.Goods.Option;

public record WincubeProductResponse(
        Result result,
        Value value
) {
    public static final String SUCCESS_CODE = "0";

    public boolean isSuccess() {
        return result.code.equals(SUCCESS_CODE);
    }

    record Result(
            String code,
            String goodsNum
    ) {
    }

    record Value(
            List<Map<String, Object>> goodslist
    ) {
        private static final DateTimeFormatter PERIOD_END_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

        record Goods(
                String goodsId,
                String affiliate,  // 상품분류 (GS25)
                String affiliateCategory,  // 교환처분류 (편의점)
                String desc, // 상품설명
                String goodsNm, // 상품명

                @JsonFormat(pattern = "yyyyMMdd")
                LocalDate period_end, // 상품판매종료일 (20291231)
                int limitDate, // 유효기간 (60)
                List<Option> options
        ) {
            record Option(
                    String name, // 옵션 이름
                    String code  // 옵션코드
            ) {
            }
        }

        public List<Goods> goodsList() {
            List<Goods> goodsList = new ArrayList<>();
            for (Map<String, Object> goodsMap : goodslist) {
                goodsList.add(new Goods(
                        (String) goodsMap.get("goods_id"),
                        (String) goodsMap.get("affiliate"),
                        (String) goodsMap.get("affiliate_category"),
                        (String) goodsMap.get("desc"),
                        (String) goodsMap.get("goods_nm"),
                        LocalDate.parse((String) goodsMap.get("period_end"), PERIOD_END_FORMATTER),
                        (Integer) goodsMap.get("limit_date"),
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
}

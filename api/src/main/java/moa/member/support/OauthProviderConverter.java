package moa.member.support;

import moa.member.domain.OauthId.OauthProvider;
import org.springframework.core.convert.converter.Converter;

public class OauthProviderConverter implements Converter<String, OauthProvider> {

    @Override
    public OauthProvider convert(String source) {
        return OauthProvider.fromName(source);
    }
}

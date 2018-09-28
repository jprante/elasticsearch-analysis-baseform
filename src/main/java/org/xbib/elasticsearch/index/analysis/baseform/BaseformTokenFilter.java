package org.xbib.elasticsearch.index.analysis.baseform;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;
import org.xbib.elasticsearch.common.fsa.Dictionary;

public class BaseformTokenFilter extends TokenFilter {

	private static final Logger LOG = LogManager.getLogger(BaseformTokenFilter.class);

    private static ConcurrentHashMap<CharSequence, CharSequence> TERM_CACHE;

    private static final AtomicLong termCacheCount = new AtomicLong(0);

    private static final AtomicBoolean needsClearCache = new AtomicBoolean(false); 

    private static long MAX_CACHE_SIZE;

    private static final CharSequence NO_TERMS = "";

    private final LinkedList<PackedTokenAttributeImpl> tokens;

    private final Dictionary dictionary;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    private AttributeSource.State current;

    protected BaseformTokenFilter(TokenStream input, Dictionary dictionary, long maxCacheSize) {
        super(input);
        this.tokens = new LinkedList<>();
        this.dictionary = dictionary;        if (TERM_CACHE == null) {
    			TERM_CACHE = new ConcurrentHashMap<CharSequence, CharSequence>();
    			MAX_CACHE_SIZE = maxCacheSize;
        }
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!tokens.isEmpty()) {
            assert current != null;
            PackedTokenAttributeImpl token = tokens.removeFirst();
            restoreState(current);
            termAtt.setEmpty().append(token);
            posIncAtt.setPositionIncrement(0);
            return true;
        }
        if (input.incrementToken()) {
            baseform();
            if (!tokens.isEmpty()) {
                current = captureState();
            }
            return true;
        } else {
            return false;
        }
    }

    protected void baseform() throws CharacterCodingException {
        CharSequence term = new String(termAtt.buffer(), 0, termAtt.length());
        if (needsClearCache.get()) {
        		checkCacheSize();
        }
		CharSequence s = TERM_CACHE.computeIfAbsent(term, t -> {
			if (termCacheCount.incrementAndGet() > MAX_CACHE_SIZE) {
				needsClearCache.set(true);
			}

			try {
				CharSequence baseform = dictionary.lookup(t);
				if (baseform == null) {
					return NO_TERMS;
				}
				return baseform;
			} catch (CharacterCodingException e) {
				return NO_TERMS;
			}
		});
        if (s != null && s.length() > 0) {
            PackedTokenAttributeImpl impl = new PackedTokenAttributeImpl();
            impl.append(s);
            tokens.add(impl);
        }
    }
    
	private void checkCacheSize() {
		needsClearCache.set(false);
		final Runtime runtime = Runtime.getRuntime();
		long memoryUsage = runtime.totalMemory() - runtime.freeMemory();
		TERM_CACHE = new ConcurrentHashMap<CharSequence, CharSequence>();
		termCacheCount.set(0);
		LOG.warn("Clearing term cache for baseform, memory usage: " + memoryUsage);
	}

    @Override
    public void reset() throws IOException {
        super.reset();
        tokens.clear();
        current = null;
    }

    public void close() throws IOException {
        super.close();
        tokens.clear();
        current = null;
    }

}

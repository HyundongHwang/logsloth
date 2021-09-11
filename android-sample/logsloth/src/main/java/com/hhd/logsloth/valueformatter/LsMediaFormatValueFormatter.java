package com.hhd.logsloth.valueformatter;

import android.media.AudioFormat;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.hhd.logsloth.LogSloth;

public class LsMediaFormatValueFormatter implements LogSloth.IValueFormatter {

    @Override
    public boolean canFormat(Object value) {
        boolean res = value instanceof MediaFormat;
        return res;
    }

    @Override
    public String format(Object value) {
        StringBuffer sb = new StringBuffer();
        MediaFormat format = (MediaFormat) value;

        //사용할 AAC 프로파일을 설명하는 키입니다 (AAC 오디오 형식 만 해당).
        //
        //상수는 android.media.MediaCodecInfo.CodecProfileLevel}에서 선언됩니다.
        if (format.containsKey(MediaFormat.KEY_AAC_PROFILE)) {
            int AAC_PROFILE = format.getInteger(MediaFormat.KEY_AAC_PROFILE);

            switch (AAC_PROFILE) {
                case MediaCodecInfo.CodecProfileLevel.AACObjectMain:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectMain\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectLC:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectLC\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectSSR:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectSSR\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectLTP:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectLTP\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectScalable:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectScalable\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectERLC:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectERLC\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectERScalable:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectERScalable\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectLD:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectLD\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectHE_PS:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectHE_PS\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectELD:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectELD\n", AAC_PROFILE));
                    break;
                case MediaCodecInfo.CodecProfileLevel.AACObjectXHE:
                    sb.append(String.format("AAC_PROFILE : %d : AACObjectXHE\n", AAC_PROFILE));
                    break;
                default:
                    sb.append(String.format("AAC_PROFILE : %d\n", AAC_PROFILE));
                    break;

            }
        }


        //AAC 디코더가 출력 할 수있는 최대 채널 수를 설명하는 키입니다.
        //기본적으로 디코더는 인코딩 된 것과 동일한 수의 채널을 출력합니다
        //스트림 (지원되는 경우). 출력 채널 수를 제한하려면이 값을 설정
        if (format.containsKey(MediaFormat.KEY_AAC_MAX_OUTPUT_CHANNEL_COUNT)) {
            int AAC_MAX_OUTPUT_CHANNEL_COUNT = format.getInteger(MediaFormat.KEY_AAC_MAX_OUTPUT_CHANNEL_COUNT);
            sb.append(String.format("AAC_MAX_OUTPUT_CHANNEL_COUNT : %d\n", AAC_MAX_OUTPUT_CHANNEL_COUNT));
        }

        //터널링 된 비디오 코덱과 연결된 AudioTrack의 오디오 세션 ID를 설명하는 키입니다.
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_AUDIO_SESSION_ID)) {
            int AUDIO_SESSION_ID = format.getInteger(MediaFormat.KEY_AUDIO_SESSION_ID);
            sb.append(String.format("AUDIO_SESSION_ID : %d\n", AUDIO_SESSION_ID));
        }

        //사용할 AAC SBR 모드를 설명하는 키 (AAC 오디오 형식 만 해당).
        //연관된 값은 정수이며 다음 값으로 설정할 수 있습니다.
        //
        //0 - SBR을 적용하지 않아야합니다.
        //1 - 단일 요율 SBR
        //2 - 2 배 비율 SBR
        //
        //참고 :이 키가 정의되지 않은 경우 원하는 AAC 프로파일에 대한 기본 SRB 모드가 사용됩니다.
        //이 키는 인코딩 중에 만 사용됩니다.
        if (format.containsKey(MediaFormat.KEY_AAC_SBR_MODE)) {
            int SBR_MODE = format.getInteger(MediaFormat.KEY_AAC_SBR_MODE);

            switch (SBR_MODE) {
                case 0:
                    sb.append(String.format("SBR_MODE : %d no SBR\n", SBR_MODE));
                    break;
                case 1:
                    sb.append(String.format("SBR_MODE : %d single rate mode\n", SBR_MODE));
                    break;
                case 2:
                    sb.append(String.format("SBR_MODE : %d double rate mode\n", SBR_MODE));
                    break;
            }
        }

        //평균 비트율을 비트/초 단위로 나타내는 키입니다.
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
            int BIT_RATE = format.getInteger(MediaFormat.KEY_BIT_RATE);
            sb.append(String.format("BIT_RATE : %d\n", BIT_RATE));
        }

        //프레임/초 단위의 비디오 포맷 캡처 속도를 설명하는 키.
        //캡처 속도가 프레임 속도와 다른 경우 재생과 다른 속도로 비디오를 가져와 재생 중 느린 동작 또는 시간 경과 효과가 발생 함을 의미합니다.
        //응용 프로그램은이 키의 값을 사용하여 비디오가 기록 될 때 캡처 속도와 재생 속도 간의 상대 속도 비율을 알릴 수 있습니다.
        //연관된 값은 정수 또는 부동 소수점입니다.
        if (format.containsKey(MediaFormat.KEY_CAPTURE_RATE)) {
            double CAPTURE_RATE = format.getInteger(MediaFormat.KEY_CAPTURE_RATE);
            sb.append(String.format("CAPTURE_RATE : %f\n", CAPTURE_RATE));
        }

        //오디오 형식의 채널 수를 설명하는 키입니다.
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
            int CHANNEL_COUNT = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            sb.append(String.format("CHANNEL_COUNT : %d\n", CHANNEL_COUNT));
        }

        //오디오 내용의 채널 구성을 설명하는 키입니다.
        //이 마스크는 android.media.AudioFormat의 채널 마스크 정의에서 가져온 비트로 구성됩니다.
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_CHANNEL_MASK)) {
            int CHANNEL_MASK = format.getInteger(MediaFormat.KEY_CHANNEL_MASK);

            switch (CHANNEL_MASK) {
                case AudioFormat.CHANNEL_INVALID:
                    sb.append(String.format("CHANNEL_MASK : %d : CHANNEL_INVALID\n", CHANNEL_MASK));
                    break;
                case AudioFormat.CHANNEL_OUT_DEFAULT:
                    sb.append(String.format("CHANNEL_MASK : %d : CHANNEL_OUT_DEFAULT\n", CHANNEL_MASK));
                    break;
                case AudioFormat.CHANNEL_OUT_FRONT_LEFT:
                    sb.append(String.format("CHANNEL_MASK : %d : CHANNEL_OUT_FRONT_LEFT\n", CHANNEL_MASK));
                    break;
                case AudioFormat.CHANNEL_OUT_FRONT_RIGHT:
                    sb.append(String.format("CHANNEL_MASK : %d : CHANNEL_OUT_FRONT_RIGHT\n", CHANNEL_MASK));
                    break;
                case AudioFormat.CHANNEL_OUT_FRONT_CENTER:
                    sb.append(String.format("CHANNEL_MASK : %d : CHANNEL_OUT_FRONT_CENTER\n", CHANNEL_MASK));
                    break;
                default:
                    sb.append(String.format("CHANNEL_MASK : %d\n", CHANNEL_MASK));
                    break;
            }
        }

        //비디오 형식으로 내용의 색상 형식을 설명하는 키입니다.
        //상수는 android.media.MediaCodecInfo.CodecCapabilities에서 선언됩니다.
        if (format.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
            int COLOR_FORMAT = format.getInteger(MediaFormat.KEY_COLOR_FORMAT);

            switch (COLOR_FORMAT) {
                case MediaCodecInfo.CodecCapabilities.COLOR_Format24bitBGR888:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_Format24bitBGR888\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_Format24bitARGB1887:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_Format24bitARGB1887\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_Format25bitARGB1888:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_Format25bitARGB1888\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_Format32bitBGRA8888:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_Format32bitBGRA8888\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_Format32bitARGB8888:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_Format32bitARGB8888\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_FormatYUV411Planar\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_FormatYUV411PackedPlanar\n", COLOR_FORMAT));
                    break;
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                    sb.append(String.format("COLOR_FORMAT : %d : COLOR_FormatYUV420Planar\n", COLOR_FORMAT));
                    break;
                default:
                    sb.append(String.format("COLOR_FORMAT : %d\n", COLOR_FORMAT));
                    break;
            }
        }

        //비디오 내용의 구성 요소 값 범위를 설명하는 선택적 키.
        //연관된 값은 정수입니다.
        //지정되지 않은 경우 0 또는 COLOR_RANGE_ 값 중 하나입니다.
        if (format.containsKey(MediaFormat.KEY_COLOR_RANGE)) {
            int COLOR_RANGE = format.getInteger(MediaFormat.KEY_COLOR_RANGE);

            switch (COLOR_RANGE) {
                //제한된 범위.
                //Y 구성 요소 값의 범위는 8 비트 내용의 경우 16 ~ 235입니다.
                //Cr, Cy 값의 범위는 8 비트 콘텐츠의 경우 16 ~ 240입니다.
                //비디오 내용의 기본값입니다.
                case MediaFormat.COLOR_RANGE_LIMITED:
                    sb.append(String.format("COLOR_RANGE : %d : COLOR_RANGE_LIMITED\n", COLOR_RANGE));
                    break;
                //전체 범위.
                //Y, Cr 및 Cb 구성 요소 값의 범위는 8 비트 내용의 경우 0에서 255까지입니다.
                case MediaFormat.COLOR_RANGE_FULL:
                    sb.append(String.format("COLOR_RANGE : %d : COLOR_RANGE_FULL\n", COLOR_RANGE));
                    break;
                default:
                    sb.append(String.format("COLOR_RANGE : %d\n", COLOR_RANGE));
                    break;
            }
        }

        //다음 색상 애스펙트 값은 HardwareAPI.h의 색상 애스펙트 값과 동기화되어야합니다.
        //비디오 컨텐츠의 색 원색, 흰색 점 및 광도 요소를 설명하는 선택적 키.
        //연관된 값은 정수입니다.
        //지정되지 않은 경우 0 또는 COLOR_STANDARD_ 값 중 하나입니다.
        if (format.containsKey(MediaFormat.KEY_COLOR_STANDARD)) {
            int COLOR_STANDARD = format.getInteger(MediaFormat.KEY_COLOR_STANDARD);

            switch (COLOR_STANDARD) {
                case MediaFormat.COLOR_STANDARD_BT709:
                    sb.append(String.format("COLOR_STANDARD : %d : COLOR_STANDARD_BT709\n", COLOR_STANDARD));
                    break;
                case MediaFormat.COLOR_STANDARD_BT601_PAL:
                    sb.append(String.format("COLOR_STANDARD : %d : COLOR_STANDARD_BT601_PAL\n", COLOR_STANDARD));
                    break;
                case MediaFormat.COLOR_STANDARD_BT601_NTSC:
                    sb.append(String.format("COLOR_STANDARD : %d : COLOR_STANDARD_BT601_NTSC\n", COLOR_STANDARD));
                    break;
                case MediaFormat.COLOR_STANDARD_BT2020:
                    sb.append(String.format("COLOR_STANDARD : %d : COLOR_STANDARD_BT2020\n", COLOR_STANDARD));
                    break;
                default:
                    sb.append(String.format("COLOR_STANDARD : %d\n", COLOR_STANDARD));
                    break;
            }
        }

        //인코딩 복잡성을 설명하는 키입니다.
        //연관된 값은 정수입니다.
        //이 값은 장치 및 코덱에 따라 다릅니다.
        //일반적으로 값이 낮을수록 속도가 빠르거나 전력 소모가 적은 인코딩이됩니다.
        if (format.containsKey(MediaFormat.KEY_COMPLEXITY)) {
            int COMPLEXITY = format.getInteger(MediaFormat.KEY_COMPLEXITY);
            sb.append(String.format("COMPLEXITY : %d\n", COMPLEXITY));
        }

        //내용의 길이 (마이크로 초)를 설명하는 키.
        //연관된 값은 long입니다.
        if (format.containsKey(MediaFormat.KEY_DURATION)) {
            long DURATION = format.getLong(MediaFormat.KEY_DURATION);
            sb.append(String.format("DURATION : %d\n", DURATION));
        }

        //비디오 형식의 프레임 속도를 초당 프레임 수로 나타내는 키입니다.
        //
        //관련 값은 일반적으로 플랫폼에서 값을 사용할 때 정수입니다.
        //그러나 비디오 코덱은 부동 구성 값을 허용합니다.
        //
        //구체적으로, MediaExtractor # getTrackFormat MediaExtractor는, 지정되고 0이 아닌 경우, 트럭의 frame rate 정보에 대응하는 정수 치를 제공합니다.
        //
        //그렇지 않으면이 키가 없습니다. MediaCodec # configure MediaCodec}는, float와 integer의 양쪽 모두의 값을 받아들입니다.
        //
        //이것은 {KEY_OPERATING_RATE}가 존재하지 않고, KEY_PRIORITY가 {@code 0} (realtime) 인 경우에 희망의 조작 프레임 레이트를 나타냅니다.
        //
        //비디오 인코더의 경우,이 값은 의도 된 프레임 레이트에 대응하지만, 인코더는 MediaCodec에 근거한 가변 프레임 레이트를 지원할 예정이다.
        //
        //BufferInfo # presentationTimeUs 버퍼 타임 스탬프}.
        //
        //이 키는 {@code MediaCodec} {@link MediaCodec # getInputFormat 입력} / {@ link MediaCodec # getOutputFormat 출력} 형식이나 {@link MediaMuxer # addTrack MediaMuxer} 형식으로 사용되지 않습니다.
        if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
            int FRAME_RATE = format.getInteger(MediaFormat.KEY_FRAME_RATE);
            sb.append(String.format("FRAME_RATE : %d\n", FRAME_RATE));
        }

        //동영상 형식의 콘텐츠 높이를 나타내는 키입니다.
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_HEIGHT)) {
            int HEIGHT = format.getInteger(MediaFormat.KEY_HEIGHT);
            sb.append(String.format("HEIGHT : %d\n", HEIGHT));
        }

        //키 프레임 사이의 초 단위로 표시된 키 프레임의 빈도를 설명하는 키입니다.
        //
        //이 키는 비디오 인코더에서 사용됩니다.
        //
        //음수 값은 첫 번째 프레임 이후 키 프레임이 요청되지 않음을 의미합니다.
        //
        //0 값은 모든 키 프레임을 포함하는 스트림이 요청되었음을 의미합니다.
        //
        //대부분의 비디오 인코더는 {@linkplain #KEY_FRAME_RATE frame rate} 정보를 사용하여 키 프레임간에 비 키 프레임 수의이 값을 변환합니다.
        //
        //따라서 실제 프레임 속도가 다른 경우 (예 : 입력 프레임이 떨어지거나 프레임 속도가 변경되는 경우) 키 프레임 간의 시간 간격은 구성된 값이 아닙니다.
        //
        //관련된 값은 정수입니다 (또는 {@link android.os.Build.VERSION_CODES # N_MR1} 이후로 float).
        if (format.containsKey(MediaFormat.KEY_I_FRAME_INTERVAL)) {
            int I_FRAME_INTERVAL = format.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL);
            sb.append(String.format("I_FRAME_INTERVAL : %d\n", I_FRAME_INTERVAL));
        }

        //프레임에서 인트라 새로 고침의 기간을 설명하는 선택적 키.
        //
        //이것은 비디오 인코더에만 적용되는 선택적 매개 변수입니다. 인코더가 그것을 서포트하고 있으면 ({MediaCodecInfo.CodecCapabilities # FEATURE_IntraRefresh}), 지정된 기간 후에 전체 프레임이 완전 갱신됩니다.
        //
        //또한 각 프레임에 대해 매크로 블록의 수정 된 부분 집합은 키 프레임을 삽입하는 것보다 더 일정한 비트 전송률을 초래하는 인트라 코딩되어야합니다.
        //
        //이 키는 지연이 적고 오류를 방지 할 수있는 비디오 스트리밍 응용 프로그램에 권장됩니다.
        //
        //비디오 인코더가 인트라 새로 고침 기능을 지원하지 않으면이 키는 무시됩니다. 출력 형식을 사용하여이 기능이 활성화되었는지 확인하십시오.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_INTRA_REFRESH_PERIOD)) {
            int INTRA_REFRESH_PERIOD = format.getInteger(MediaFormat.KEY_INTRA_REFRESH_PERIOD);
            sb.append(String.format("INTRA_REFRESH_PERIOD : %d\n", INTRA_REFRESH_PERIOD));
        }

        //콘텐트가 AAC 오디오이고 오디오 프레임이 ADTS 헤더로 시작되는 경우 값 1로 매핑되는 키.
        //
        //연관된 값은 정수 (0 또는 1)입니다.
        //
        //이 키는 _decoding_ 콘텐츠에서만 지원되며 ADTS 출력을 내보내는 인코더를 구성하는 데 사용할 수 없습니다.
        if (format.containsKey(MediaFormat.KEY_IS_ADTS)) {
            int IS_ADTS = format.getInteger(MediaFormat.KEY_IS_ADTS);
            sb.append(String.format("IS_ADTS : %d\n", IS_ADTS));
        }

        //트랙의 boolean AUTOSELECT 동작을위한 키.
        //
        //AUTOSELECT = true 인 트랙은 현재 로캘을 기반으로 특정 사용자 선택없이 트랙을 자동으로 선택할 때 고려됩니다.
        //
        //현재 사용자가 자막 로캘에 '기본값'을 선택한 경우 자막 트랙에만 사용됩니다.
        //
        //연관된 값은 정수이며, 0이 아닌 값은 TRUE를 의미합니다.
        //
        //이것은 선택적 필드입니다.
        //
        //지정하지 않으면 AUTOSELECT의 기본값은 TRUE입니다.
        if (format.containsKey(MediaFormat.KEY_IS_AUTOSELECT)) {
            int IS_AUTOSELECT = format.getInteger(MediaFormat.KEY_IS_AUTOSELECT);
            sb.append(String.format("IS_AUTOSELECT : %d\n", IS_AUTOSELECT));
        }

        //트랙의 불리언 값 DEFAULT 동작을위한 키.
        //
        //DEFAULT = true 인 트랙은 특정 사용자가 선택하지 않은 경우 선택됩니다.
        //
        //이것은 현재 두 가지 시나리오에서 사용됩니다.
        //
        //1) 자막 로캘에 사용자가 '기본값'을 선택한 경우
        //
        //2) 이미지가 파일의 주 항목 인 것을 나타내는 #MIMETYPE_IMAGE_ANDROID_HEIC} 트랙을 표시합니다.
        //
        //연관된 값은 정수이며, 0이 아닌 값은 TRUE를 의미합니다.
        //
        //이것은 선택적 필드입니다. 지정하지 않으면 DEFAULT가 FALSE로 간주됩니다.
        if (format.containsKey(MediaFormat.KEY_IS_DEFAULT)) {
            int IS_DEFAULT = format.getInteger(MediaFormat.KEY_IS_DEFAULT);
            sb.append(String.format("IS_DEFAULT : %d\n", IS_DEFAULT));
        }

        //자막 트랙의 FORCED 필드의 키입니다.
        //
        //강제 자막 트랙 인 경우 True입니다.
        //
        //강제 자막 트랙은 콘텐츠에 필수적이며 사용자가 자막을 끈 경우에도 표시됩니다.
        //
        //예를 들어 외국 / 외계인 대화 또는 표지판을 번역하는 데 사용됩니다.
        //
        //연관된 값은 정수이며, 0이 아닌 값은 TRUE를 의미합니다.
        //
        //이것은 선택적 필드입니다.
        //
        //지정하지 않으면 FORCED가 FALSE로 기본 설정됩니다.
        if (format.containsKey(MediaFormat.KEY_IS_FORCED_SUBTITLE)) {
            int IS_FORCED_SUBTITLE = format.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE);
            sb.append(String.format("IS_FORCED_SUBTITLE : %d\n", IS_FORCED_SUBTITLE));
        }

        //ISO 639-1 또는 639-2 / T 코드를 사용하여 콘텐츠의 언어를 설명하는 키입니다.
        //
        //연관된 값은 문자열입니다.
        if (format.containsKey(MediaFormat.KEY_LANGUAGE)) {
            String LANGUAGE = format.getString(MediaFormat.KEY_LANGUAGE);
            sb.append(String.format("LANGUAGE : %s\n", LANGUAGE));
        }

        //프레임에서 원하는 엔코더 대기 시간을 설명하는 선택적 키.
        //
        //이것은 비디오 인코더에만 적용되는 선택적 매개 변수입니다.
        //
        //인코더가 이것을 지원하면 지정된 수의 프레임을 대기열에 넣은 후 적어도 하나의 출력 프레임을 출력해야합니다.
        //
        //비디오 엔코더가 대기 시간 기능을 지원하지 않으면이 키는 무시됩니다.
        //
        //출력 형식을 사용하여이 기능이 활성화되었는지와 인코더가 사용하는 실제 값을 확인하십시오.
        //
        //키가 지정되지 않은 경우 기본 대기 시간은 적용 관련 사항입니다.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_LATENCY)) {
            int LATENCY = format.getInteger(MediaFormat.KEY_LATENCY);
            sb.append(String.format("LATENCY : %d\n", LATENCY));
        }

        //엔코더에 의해 사용되는 요구 된 프로파일을 기술하는 키.
        //
        //연관된 값은 정수입니다.
        //
        //상수는 MediaCodecInfo.CodecProfileLevel에서 선언됩니다.
        //
        //이 키는 원하는 프로필을 지정할 때 추가 힌트로 사용되며 레벨을 지정하는 코덱에만 지원됩니다.
        //
        //이 키는, KEY_PROFILE 프로파일이 지정되어 있지 않은 경우는 무시됩니다.
        //
        //MediaCodecInfo.CodecCapabilities # profileLevels
        //
        //인코딩 된 데이터 형식으로 정의 된 지원되는 프로필 / 수준 조합을 열거합니다.
        //
        //이러한 조합은 비디오 해상도, 비트 전송률에 제한을 부과하고 B 프레임 지원, 산술 코딩 등과 같은 사용 가능한 인코더 도구를 제한합니다.
        //
        //        // from OMX_VIDEO_AVCLEVELTYPE
        //        public static final int AVCLevel1       = 0x01;
        //        public static final int AVCLevel1b      = 0x02;
        //        public static final int AVCLevel11      = 0x04;
        //        public static final int AVCLevel12      = 0x08;
        //        public static final int AVCLevel13      = 0x10;
        //        public static final int AVCLevel2       = 0x20;
        //        public static final int AVCLevel21      = 0x40;
        //        public static final int AVCLevel22      = 0x80;
        //        public static final int AVCLevel3       = 0x100;
        //
        //        // from OMX_VIDEO_H263LEVELTYPE
        //        public static final int H263Level10      = 0x01;
        //        public static final int H263Level20      = 0x02;
        //        public static final int H263Level30      = 0x04;
        //        public static final int H263Level40      = 0x08;
        //        public static final int H263Level45      = 0x10;
        //
        //        // from OMX_VIDEO_MPEG4LEVELTYPE
        //        public static final int MPEG4Level0      = 0x01;
        //        public static final int MPEG4Level0b     = 0x02;
        //        public static final int MPEG4Level1      = 0x04;
        //        public static final int MPEG4Level2      = 0x08;
        //        public static final int MPEG4Level3      = 0x10;
        //
        //        // from OMX_VIDEO_VP9LEVELTYPE
        //        public static final int VP9Level1  = 0x1;
        //        public static final int VP9Level11 = 0x2;
        //        public static final int VP9Level2  = 0x4;
        //        public static final int VP9Level21 = 0x8;
        //        public static final int VP9Level3  = 0x10;
        //        public static final int VP9Level31 = 0x20;
        //
        //        // from OMX_VIDEO_HEVCLEVELTYPE
        //        public static final int HEVCMainTierLevel1  = 0x1;
        //        public static final int HEVCHighTierLevel1  = 0x2;
        //        public static final int HEVCMainTierLevel2  = 0x4;
        //        public static final int HEVCHighTierLevel2  = 0x8;
        //        public static final int HEVCMainTierLevel21 = 0x10;
        //        public static final int HEVCHighTierLevel21 = 0x20;
        //        public static final int HEVCMainTierLevel3  = 0x40;
        //        public static final int HEVCHighTierLevel3  = 0x80;
        //        public static final int HEVCMainTierLevel31 = 0x100;
        //        public static final int HEVCHighTierLevel31 = 0x200;
        if (format.containsKey(MediaFormat.KEY_LEVEL)) {
            int LEVEL = format.getInteger(MediaFormat.KEY_LEVEL);
            sb.append(String.format("LEVEL : %d\n", LEVEL));
        }

        //비디오 내용에 해상도가 변경된 경우를 대비하여 비디오 디코더 형식의 최대 예상 높이를 나타내는 키입니다.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_MAX_HEIGHT)) {
            int MAX_HEIGHT = format.getInteger(MediaFormat.KEY_MAX_HEIGHT);
            sb.append(String.format("MAX_HEIGHT : %d\n", MAX_HEIGHT));
        }

        //MediaFormat의 MIME 유형을 설명하는 키입니다.
        //
        //연관된 값은 문자열입니다.
        if (format.containsKey(MediaFormat.KEY_MIME)) {
            String MIME = format.getString(MediaFormat.KEY_MIME);
            sb.append(String.format("MIME : %s\n", MIME));
        }

        //코덱이 작동해야하는 오디오의 비디오 또는 샘플 속도에 대해 원하는 작동 프레임 속도를 설명하는 키.
        //
        //연관된 값은 초당 프레임 수 또는 초당 샘플 수를 나타내는 정수 또는 부동 소수입니다.
        //
        //이는 비디오 인코더 형식에 목표 재생 속도 (예 : 30fps)가 포함 된 고속 / 슬로우 모션 비디오 캡처와 같은 경우에 사용되지만 구성 요소는 높은 작동 캡처 속도 (예 : 240fps)를 처리 할 수 있어야합니다.
        //
        //이 속도는 코덱에서 자원 계획 및 작동 지점 설정에 사용됩니다.
        if (format.containsKey(MediaFormat.KEY_OPERATING_RATE)) {
            float OPERATING_RATE = format.getFloat(MediaFormat.KEY_OPERATING_RATE);
            sb.append(String.format("OPERATING_RATE : %f\n", OPERATING_RATE));
        }

        //비 표시 순서 코드 프레임의 최대 수를 설명하는 선택적 키.
        //
        //이것은 비디오 인코더에만 적용되는 선택적 매개 변수입니다.
        //
        //응용 프로그램은 출력 형식에서이 키의 값을 확인하여 코덱이 표시되지 않은 코딩 된 프레임을 생성하는지 확인해야합니다.
        //
        //인코더가 이것을 서포트하고있는 경우, 출력 프레임의 순서는 표시 순서와 다르고, 각 프레임의 표시 순서는 MediaCodec.BufferInfo # presentationTimeUs로부터 취득 할 수 있습니다.
        //
        //API 레벨 27 이전에는 애플리케이션이 요청하지 않은 경우에도 표시되지 않은 코딩 된 프레임을 수신 할 수 있습니다.
        //
        //주 : 어플리케이션은, 순서를 표시하는 프레임을, MediaMuxer # writeSampleData에 공급하기 전에 재 배열해서는 안됩니다.
        //
        //기본값은 0입니다.
        if (format.containsKey(MediaFormat.KEY_OUTPUT_REORDER_DEPTH)) {
            int OUTPUT_REORDER_DEPTH = format.getInteger(MediaFormat.KEY_OUTPUT_REORDER_DEPTH);
            sb.append(String.format("OUTPUT_REORDER_DEPTH : %d\n", OUTPUT_REORDER_DEPTH));
        }

        //원시 오디오 샘플 인코딩 / 형식을 설명하는 키입니다.
        //
        //연관된 값은 정수이며,
        //
        //AudioFormat} .ENCODING_PCM_ 값 중 하나를 사용하여.
        //
        //MediaCodec # configure MediaCodec.configure (& hellip;)} 호출 중에 원하는 원시 오디오 샘플 형식을 지정하는 오디오 디코더 및 인코더의 선택적 키입니다.
        //
        //실제 형식을 확인하려면 MediaCodec # getInputFormat MediaCodec.getInput} / {@link MediaCodec # getOutputFormat OutputFormat (& hellip;)}을 사용하십시오.
        //
        //PCM 디코더의 경우이 키는 입력 및 출력 샘플 인코딩을 지정합니다.
        //
        //이 키는, 지정된 경우, 오디오 데이터의 샘플 형식을 지정하기 위해서 (때문에) MediaExtractor에 의해 사용됩니다.
        //
        //이 키가없는 경우 원시 오디오 샘플 형식은 16 비트 길이로 서명됩니다.
        if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
            int PCM_ENCODING = format.getInteger(MediaFormat.KEY_PCM_ENCODING);

            switch (PCM_ENCODING) {
                case AudioFormat.ENCODING_PCM_16BIT:
                    sb.append(String.format("PCM_ENCODING : %d : ENCODING_PCM_16BIT\n", PCM_ENCODING));
                    break;
                case AudioFormat.ENCODING_PCM_8BIT:
                    sb.append(String.format("PCM_ENCODING : %d : ENCODING_PCM_8BIT\n", PCM_ENCODING));
                    break;
                case AudioFormat.ENCODING_PCM_FLOAT:
                    sb.append(String.format("PCM_ENCODING : %d : ENCODING_PCM_FLOAT\n", PCM_ENCODING));
                    break;
                default:
                    sb.append(String.format("PCM_ENCODING : %d\n", PCM_ENCODING));
                    break;
            }
        }

        //원하는 코덱 우선 순위를 설명하는 키입니다.
        //
        //연관된 값은 정수입니다.
        //
        //값이 높을수록 우선 순위가 낮습니다.
        //
        //현재 두 가지 레벨 만 지원됩니다.
        //
        //0 : 실시간 우선 순위 - 코덱이 실시간으로 주어진 성능 구성 (예 : 프레임 속도)을 지원해야 함을 의미합니다.
        //
        //최적의 성능이 적절하지 않은 경우에만 미디어 재생, 캡처 및 실시간 통신 시나리오를 사용해야합니다.
        //
        //1 : 비 실시간 우선 순위 (최선의 노력).
        //
        //이것은 코덱 구성 및 자원 계획에서 응용 프로그램의 실시간 요구 사항을 이해하는 데 사용되는 힌트입니다. 그러나 미디어 구성 요소의 특성상 성능이 보장되지는 않습니다.
        if (format.containsKey(MediaFormat.KEY_PRIORITY)) {
            int PRIORITY = format.getInteger(MediaFormat.KEY_PRIORITY);

            switch (PRIORITY) {
                case 0:
                    sb.append(String.format("PRIORITY : %d : realtime priority \n", PRIORITY));
                    break;
                case 1:
                    sb.append(String.format("PRIORITY : %d : non-realtime priority \n", PRIORITY));
                    break;
            }
        }


        //엔코더에 의해 사용되는 요구 된 프로파일을 기술하는 키.
        //
        //연관된 값은 정수입니다.
        //
        //상수는 MediaCodecInfo.CodecProfileLevel에서 선언됩니다.
        //
        //이 키는 힌트로 사용되며 프로필을 지정하는 코덱에서만 지원됩니다.
        //
        //참고 : 코덱은 지정된 프로필에서 사용 가능한 모든 코딩 도구를 자유롭게 사용할 수 있습니다.
        //
        //MediaCodecInfo.CodecCapabilities # profileLevels를 참조하십시오.
        //
        //        // from OMX_VIDEO_AVCPROFILETYPE
        //        public static final int AVCProfileBaseline = 0x01;
        //        public static final int AVCProfileMain     = 0x02;
        //        public static final int AVCProfileExtended = 0x04;
        //        public static final int AVCProfileHigh     = 0x08;
        //
        //        // from OMX_VIDEO_H263PROFILETYPE
        //        public static final int H263ProfileBaseline             = 0x01;
        //        public static final int H263ProfileH320Coding           = 0x02;
        //        public static final int H263ProfileBackwardCompatible   = 0x04;
        //        public static final int H263ProfileISWV2                = 0x08;
        //
        //        // from OMX_VIDEO_MPEG4PROFILETYPE
        //        public static final int MPEG4ProfileSimple              = 0x01;
        //        public static final int MPEG4ProfileSimpleScalable      = 0x02;
        //        public static final int MPEG4ProfileCore                = 0x04;
        //        public static final int MPEG4ProfileMain                = 0x08;
        //        public static final int MPEG4ProfileNbit                = 0x10;
        //
        //        // from OMX_AUDIO_AACPROFILETYPE
        //        public static final int AACObjectMain       = 1;
        //        public static final int AACObjectLC         = 2;
        //        public static final int AACObjectSSR        = 3;
        //        public static final int AACObjectLTP        = 4;
        //        public static final int AACObjectHE         = 5;
        //        public static final int AACObjectScalable   = 6;
        //        public static final int AACObjectERLC       = 17;
        //
        //        // from OMX_VIDEO_VP8PROFILETYPE
        //        public static final int VP8ProfileMain = 0x01;
        //
        //        // from OMX_VIDEO_VP9PROFILETYPE
        //        public static final int VP9Profile0 = 0x01;
        //        public static final int VP9Profile1 = 0x02;
        //        public static final int VP9Profile2 = 0x04;
        if (format.containsKey(MediaFormat.KEY_PROFILE)) {
            int PROFILE = format.getInteger(MediaFormat.KEY_PROFILE);
            sb.append(String.format("PROFILE : %d \n", PROFILE));
        }


        //원하는 인코딩 품질을 설명하는 키입니다.
        //
        //연관된 값은 정수입니다. 이 키는 정수 품질 모드로 구성된 인코더에만 지원됩니다.
        //
        //이러한 값은 장치 및 코덱에 따라 다르지만 일반적으로 값이 낮 으면 인코딩이 더 효율적 (작은 크기)이됩니다.
        //
        //MediaCodecInfo.EncoderCapabilities # getQualityRange ()를 참조하십시오.
        if (format.containsKey(MediaFormat.KEY_QUALITY)) {
            int QUALITY = format.getInteger(MediaFormat.KEY_QUALITY);
            sb.append(String.format("QUALITY : %d \n", QUALITY));
        }

        //비디오 디코더 렌더링을 서페이스로 구성 할 때 지정하면 디코더가 이전에 표시된 내용을 지우도록 중지되었을 때 디코더가 "빈"즉 표면에 검은 색 프레임을 출력하도록합니다.
        //
        //연관된 값은 값 1의 정수입니다.
        if (format.containsKey(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP)) {
            int PUSH_BLANK_BUFFERS_ON_STOP = format.getInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP);
            sb.append(String.format("PUSH_BLANK_BUFFERS_ON_STOP : %d \n", PUSH_BLANK_BUFFERS_ON_STOP));
        }

        //비디오 인코더를 "표면 입력"모드로 구성 할 때만 적용됩니다.
        //
        //관련 값은 long이며 이후 새 프레임이 사용 가능하지 않으면 이전에 엔코더에 제출 된 프레임이 반복됩니다 (1 회).
        if (format.containsKey(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER)) {
            long REPEAT_PREVIOUS_FRAME_AFTER = format.getLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER);
            sb.append(String.format("REPEAT_PREVIOUS_FRAME_AFTER : %d \n", REPEAT_PREVIOUS_FRAME_AFTER));
        }

        //출력 표면에서 원하는 시계 방향 회전을 설명하는 키입니다.
        //
        //이 키는 출력 표면을 사용하여 코덱을 구성한 경우에만 사용됩니다.
        //
        //연관된 값은도를 나타내는 정수입니다.
        //
        //지원되는 값은 0, 90, 180 또는 270입니다.
        //
        //이것은 선택적 필드입니다. 지정하지 않으면 회전은 기본적으로 0입니다.
        //
        //MediaCodecInfo.CodecCapabilities # profileLevels를 참조하십시오.
        if (format.containsKey(MediaFormat.KEY_ROTATION)) {
            int ROTATION = format.getInteger(MediaFormat.KEY_ROTATION);
            sb.append(String.format("ROTATION : %d \n", ROTATION));
        }

        //오디오 형식의 샘플 속도를 설명하는 키입니다.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
            int SAMPLE_RATE = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            sb.append(String.format("SAMPLE_RATE : %d \n", SAMPLE_RATE));
        }

        //다중 평면 (YUV) 비디오 바이트 버퍼 레이아웃의 평면 높이를 설명하는 키입니다.
        //
        //슬라이스 높이 (또는 평면 높이 / 수직 스트라이드)는 바이트 플 러의 Y 평면 상단에서 U 평면 상단으로 이동하기 위해 건너 뛸 수있는 행 수입니다.
        //
        //본질적으로 U 평면의 오프셋은 sliceHeight * stride입니다. U / V 평면의 높이는 색상 형식에 따라 계산할 수 있지만 일반적으로 정의되지는 않으며 장치 및 릴리스에 따라 다릅니다.
        //
        //연관된 값은 행 수를 나타내는 정수입니다.
        if (format.containsKey(MediaFormat.KEY_SLICE_HEIGHT)) {
            int SLICE_HEIGHT = format.getInteger(MediaFormat.KEY_SLICE_HEIGHT);
            sb.append(String.format("SLICE_HEIGHT : %d \n", SLICE_HEIGHT));
        }

        //비디오 바이트 버퍼 레이아웃의 보폭을 나타내는 키입니다.
        //
        //보폭 (또는 행 증가)은 픽셀 인덱스와 바로 아래에있는 픽셀 인덱스의 차이입니다.
        //
        //YUV 420 형식의 경우 스트라이드는 Y 평면에 해당합니다.
        //
        //U 및 V 플레인의 보폭은 컬러 포맷에 기초하여 계산 될 수 있고,
        //
        //일반적으로 정의되지는 않았지만 장치 및 릴리스에 따라 다릅니다.
        //
        //연관된 값은 바이트 수를 나타내는 정수입니다.
        if (format.containsKey(MediaFormat.KEY_STRIDE)) {
            int STRIDE = format.getInteger(MediaFormat.KEY_STRIDE);
            sb.append(String.format("STRIDE : %d \n", STRIDE));
        }

        //인코더는보다 적은 시간 계층을 지원할 수 있으며,이 경우 출력 형식에는 구성된 스키마가 포함됩니다.
        //
        //인코더가 시간 계층화를 지원하지 않으면 출력 형식에이 키가있는 항목이 없습니다.
        //
        //연관된 값은 문자열입니다.
        if (format.containsKey(MediaFormat.KEY_TEMPORAL_LAYERING)) {
            String TEMPORAL_LAYERING = format.getString(MediaFormat.KEY_TEMPORAL_LAYERING);
            sb.append(String.format("TEMPORAL_LAYERING : %s \n", TEMPORAL_LAYERING));
        }

        //미디어 트랙의 콘텐츠에 대한 고유 ID를 설명하는 키입니다.
        //
        //이 키는 MediaExtractor에 의해 사용됩니다.
        //
        //일부 추출기는 동일한 트랙의 여러 인코딩을 제공합니다 (예 : FLAC의 오디오 트랙은 FLAC이고 WAV는 MediaExtractor를 통해 두 개의 트랙으로 표현 될 수 있음).
        //
        //이전 버전과의 호환성을위한 정상 PCM 트랙, 추가 충실도를위한 플로트 PCM 트랙.
        //
        //마찬가지로 Dolby Vision 추출기는 DV 트랙의 기본 SDR 버전을 제공 할 수 있습니다.
        //
        //이 키는 동일한 기본 컨텐츠를 참조하는 MediaExtractor 트랙을 식별하는 데 사용할 수 있습니다.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_TRACK_ID)) {
            int TRACK_ID = format.getInteger(MediaFormat.KEY_TRACK_ID);
            sb.append(String.format("TRACK_ID : %d \n", TRACK_ID));
        }


        //비디오 형식으로 내용의 너비를 설명하는 키.
        //
        //연관된 값은 정수입니다.
        if (format.containsKey(MediaFormat.KEY_WIDTH)) {
            int WIDTH = format.getInteger(MediaFormat.KEY_WIDTH);
            sb.append(String.format("WIDTH : %d \n", WIDTH));
        }
        return sb.toString();
    }
}

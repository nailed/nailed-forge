#ifndef _Included_LinkedMem_h
#define _Included_LinkedMem_h

typedef struct LinkedMem{
    #ifdef WIN32
        UINT32	uiVersion;
        DWORD	uiTick;
    #else
        uint32_t uiVersion;
        uint32_t uiTick;
    #endif
    float	fAvatarPosition[3];
    float	fAvatarFront[3];
    float	fAvatarTop[3];
    wchar_t	name[256];
    float	fCameraPosition[3];
    float	fCameraFront[3];
    float	fCameraTop[3];
    wchar_t	identity[256];
    #ifdef WIN32
        UINT32	context_len;
    #else
        uint32_t context_len;
    #endif
    unsigned char context[256];
    wchar_t description[2048];
} LinkedMem_t;

static LinkedMem_t *lm;

#endif
